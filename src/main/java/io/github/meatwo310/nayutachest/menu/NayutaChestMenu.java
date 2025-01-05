package io.github.meatwo310.nayutachest.menu;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.nayutachest.block.ModBlocks;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.handler.NayutaChestDisplayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class NayutaChestMenu extends AbstractContainerMenu {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final NayutaChestBE nayutaChestBlock;
    private final ContainerLevelAccess containerLevelAccess;
    public final ContainerData containerData;
    //    private final ContainerData containerData;

    // Client
    public NayutaChestMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(
                containerId,
                playerInventory,
                playerInventory.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(NayutaChestBE.NayutaChestContainerData.DATA_SIZE)
        );
    }

    // Server
    public NayutaChestMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity, ContainerData containerData) {
        super(ModMenus.NAYUTA_CHEST_MENU.get(), containerId);
        if (blockEntity instanceof NayutaChestBE be) {
            this.nayutaChestBlock = be;
        } else {
            throw new IllegalStateException("BlockEntity is not an instance of NayutaChestBE");
        }

        this.containerLevelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.containerData = containerData;

        createPlayerHotbar(playerInventory);
        createPlayerInventory(playerInventory);
        createBESlots(be);

        addDataSlots(containerData);
    }

    private void createPlayerHotbar(Inventory playerInventory) {
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    private void createPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(
                        playerInventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        84 + row * 18
                ));
            }
        }
    }

    private void createBESlots(NayutaChestBE blockEntity) {
//        blockEntity.displayHandlerLazyOptional.ifPresent(displayHandler ->
//                this.addSlot(new SlotItemHandler(displayHandler, 0, 80, 35))
//        );
//        blockEntity.chestHandlerLazyOptional.ifPresent(chestHandler -> {
//            this.addSlot(new SlotItemHandler(chestHandler, NayutaChestHandler.SLOT_INPUT, 35, 35));
//            this.addSlot(new SlotItemHandler(chestHandler, NayutaChestHandler.SLOT_OUTPUT, 125, 35));
//        });
        blockEntity.displayHandlerLazyOptional.ifPresent(displayHandler -> {
            this.addSlot(new SlotItemHandler(displayHandler, NayutaChestDisplayHandler.SLOT_INPUT, 35, 35));
            this.addSlot(new SlotItemHandler(displayHandler, NayutaChestDisplayHandler.SLOT_OUTPUT, 125, 35));
            this.addSlot(new SlotItemHandler(displayHandler, NayutaChestDisplayHandler.SLOT_DISPLAY, 80, 35));
            LOGGER.debug("slots: {}", displayHandler.getSlots());
            LOGGER.debug("stack[0]: {}", displayHandler.getStackInSlot(0));
            LOGGER.debug("stack[1]: {}", displayHandler.getStackInSlot(1));
            LOGGER.debug("stack[2]: {}", displayHandler.getStackInSlot(2));
            nayutaChestBlock.chestHandlerLazyOptional.ifPresent(chestHandler -> {
                LOGGER.debug("be stack[0]: {}", chestHandler.getStackInSlot(0));
                LOGGER.debug("be stack[1]: {}", chestHandler.getStackInSlot(1));
            });
        });
    }

    @Override
    @NotNull
    public ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.containerLevelAccess, player, ModBlocks.NAYUTA_CHEST.get());
    }
}
