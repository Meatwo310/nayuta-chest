package io.github.meatwo310.nayutachest.blockentity;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.block.NayutaChestBlock;
import io.github.meatwo310.nayutachest.handler.NayutaChestDisplayHandler;
import io.github.meatwo310.nayutachest.handler.NayutaChestHandler;
import io.github.meatwo310.nayutachest.menu.NayutaChestMenu;
import io.github.meatwo310.nayutachest.util.IntShift;
import io.github.meatwo310.nayutachest.util.ItemStackHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.math.BigInteger;

public class NayutaChestBE extends BlockEntity implements MenuProvider {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String TITLE_KEY = "container." + NayutaChest.MODID + ".nayuta_chest";
    private static final Component TITLE = Component.translatable(TITLE_KEY);

    private final NayutaChestHandler chestHandler = new NayutaChestHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            ItemStack result = super.insertItem(slot, stack, simulate);
            if (!simulate) {
                int remaining = result.getCount();
                int inserted = stack.getCount() - remaining;
                if (inserted > 0) {
                    NayutaChestBE.this.inserted = NayutaChestBE.this.inserted.add(BigInteger.valueOf(inserted));
                }
            }
            return result;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack result = super.extractItem(slot, amount, simulate);
            if (!simulate) {
                int extracted = result.getCount();
                if (extracted > 0) {
                    NayutaChestBE.this.extracted = NayutaChestBE.this.extracted.add(BigInteger.valueOf(extracted));
                }
            }
            return result;
        }
    };
    private final NayutaChestDisplayHandler displayHandler;

    public final LazyOptional<NayutaChestHandler> chestHandlerLazyOptional = LazyOptional.of(() -> this.chestHandler);
    public LazyOptional<NayutaChestDisplayHandler> displayHandlerLazyOptional;

    private BigInteger inserted = BigInteger.ZERO;
    private BigInteger extracted = BigInteger.ZERO;
    private BigInteger insertedAvg = BigInteger.ZERO;
    private BigInteger extractedAvg = BigInteger.ZERO;

    private final ContainerData data = new NayutaChestContainerData();

    public NayutaChestBE(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.NAYUTA_CHEST.get(), blockPos, blockState);
        this.displayHandler = new NayutaChestDisplayHandler(this.chestHandler);
        this.displayHandlerLazyOptional = LazyOptional.of(() -> this.displayHandler);
    }

    public static void tick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, NayutaChestBE nayutaChestBE) {
        int frequency = 100;

        if (serverLevel.getGameTime() % frequency != 0) {
            return;
        }

        nayutaChestBE.insertedAvg = nayutaChestBE.inserted.divide(BigInteger.valueOf(frequency));
        nayutaChestBE.extractedAvg = nayutaChestBE.extracted.divide(BigInteger.valueOf(frequency));

        LOGGER.debug(
                "NayutaChestBE at: {} | profiled {} ticks | in: {} items/t | out: {} items/t",
                blockPos,
                frequency,
                nayutaChestBE.insertedAvg,
                nayutaChestBE.extractedAvg
        );

        nayutaChestBE.inserted = BigInteger.ZERO;
        nayutaChestBE.extracted = BigInteger.ZERO;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return chestHandlerLazyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        CompoundTag data = nbt.getCompound(NayutaChest.MODID);
        ItemStackHandlerUtil.load(data, "handler", this.chestHandlerLazyOptional);
        this.displayHandler.setHandler(this.chestHandler);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (ItemStackHandlerUtil.isEmpty(this.chestHandlerLazyOptional)) return;
        CompoundTag data = new CompoundTag();
        ItemStackHandlerUtil.saveAdditional(data, "handler", this.chestHandlerLazyOptional);
        nbt.put(NayutaChest.MODID, data);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        this.saveAdditional(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level == null || this.level.isClientSide()) return;
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public void setRemoved() {
        this.chestHandlerLazyOptional.invalidate();
        this.displayHandlerLazyOptional.invalidate();
        super.setRemoved();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.chestHandlerLazyOptional.invalidate();
        this.displayHandlerLazyOptional.invalidate();
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        LOGGER.debug("Opening NayutaChestMenu");

        StringBuilder itemsLog = new StringBuilder("Items: ");
        for (int i = 0; i < this.chestHandler.getSlots(); i++) {
            itemsLog.append(String.format("item[%d]:%s, ", i, this.chestHandler.getStackInSlot(i)));
        }
        LOGGER.debug(itemsLog.toString());

        StringBuilder dataLog = new StringBuilder();
        for (int i = 0; i < NayutaChestContainerData.DATA_SIZE; i++) {
            dataLog.append(String.format("ContainerData[%d]:%d, ", i, this.data.get(i)));
        }
        LOGGER.debug(dataLog.toString());

        return new NayutaChestMenu(containerId, playerInventory, this, data);
    }

    @Override
    public void saveToItem(@NotNull ItemStack p_187477_) {
        super.saveToItem(p_187477_);
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(NayutaChestBlock.FACING);
    }

    public class NayutaChestContainerData implements ContainerData {
        public static final int DATA_SIZE = 4;
        public static final int INSERTED_AVG_BASE = 0;
        public static final int INSERTED_AVG_SHIFT = 1;
        public static final int EXTRACTED_AVG_BASE = 2;
        public static final int EXTRACTED_AVG_SHIFT = 3;

        private NayutaChestContainerData() {}

        @Override
        public int get(int i) {
            return switch (i) {
                case INSERTED_AVG_BASE -> getBase(NayutaChestBE.this.insertedAvg);
                case INSERTED_AVG_SHIFT -> getShift(NayutaChestBE.this.insertedAvg);
                case EXTRACTED_AVG_BASE -> getBase(NayutaChestBE.this.extractedAvg);
                case EXTRACTED_AVG_SHIFT -> getShift(NayutaChestBE.this.extractedAvg);
                default -> 0;
            };
        }

        private static int getBase(BigInteger value) {
            // limit to 15 bits to prevent overflow in the client
            return IntShift.fromBigInteger(value, Short.SIZE - 1).base();
        }

        private static int getShift(BigInteger value) {
            // limit to 15 bits to prevent overflow in the client
            return IntShift.fromBigInteger(value, Short.SIZE - 1).shift();
        }

        @Override
        public void set(int i, int value) {
            LOGGER.warn("Setting data value {} to {} is not supported, ignoring", i, value);
        }

        @Override
        public int getCount() {
            return DATA_SIZE;
        }
    }
}
