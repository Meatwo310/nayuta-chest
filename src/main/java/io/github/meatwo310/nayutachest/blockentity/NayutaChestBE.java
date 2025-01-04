package io.github.meatwo310.nayutachest.blockentity;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.handler.NayutaChestDisplayHandler;
import io.github.meatwo310.nayutachest.handler.NayutaChestHandler;
import io.github.meatwo310.nayutachest.menu.NayutaChestMenu;
import io.github.meatwo310.nayutachest.util.ItemStackHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

        BigInteger insertedAvg = nayutaChestBE.inserted.divide(BigInteger.valueOf(frequency));
        BigInteger extractedAvg = nayutaChestBE.extracted.divide(BigInteger.valueOf(frequency));

        LOGGER.debug(
                "NayutaChestBE at: {} | in: {} items/t | out: {} items/t | profiled {} ticks",
                blockPos,
                insertedAvg,
                extractedAvg,
                frequency
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
        LogUtils.getLogger().warn("Loading NayutaChestBE: {}", data);
        ItemStackHandlerUtil.load(data, "handler", this.chestHandlerLazyOptional);
        this.displayHandler.setHandler(this.chestHandler);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        CompoundTag data = new CompoundTag();
        ItemStackHandlerUtil.saveAdditional(data, "handler", this.chestHandlerLazyOptional);
        nbt.put(NayutaChest.MODID, data);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level == null || !this.level.isClientSide()) return;
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
        return new NayutaChestMenu(containerId, playerInventory, this);
    }

    @Override
    public void saveToItem(@NotNull ItemStack p_187477_) {
        super.saveToItem(p_187477_);
    }
}
