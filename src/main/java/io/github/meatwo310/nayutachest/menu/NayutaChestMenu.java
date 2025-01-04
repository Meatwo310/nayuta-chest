package io.github.meatwo310.nayutachest.menu;

import io.github.meatwo310.nayutachest.block.ModBlocks;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.handler.NayutaChestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class NayutaChestMenu extends AbstractContainerMenu {
    private final NayutaChestBE nayutaChestBlock;
    private final ContainerLevelAccess containerLevelAccess;
//    private final ContainerData containerData;

    // Client
    public NayutaChestMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
//        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    // Server
    public NayutaChestMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
        super(ModMenus.NAYUTA_CHEST_MENU.get(), containerId);
        if (blockEntity instanceof NayutaChestBE be) {
            this.nayutaChestBlock = be;
        } else {
            throw new IllegalStateException("BlockEntity is not an instance of NayutaChestBE");
        }

        this.containerLevelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
//        this.containerData = containerData;

        createPlayerHotbar(playerInventory);
        createPlayerInventory(playerInventory);
        createBESlots(be);

//        addDataSlots(containerData);
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
        blockEntity.displayHandlerLazyOptional.ifPresent(displayHandler ->
                this.addSlot(new SlotItemHandler(displayHandler, 0, 80, 35))
        );
        blockEntity.chestHandlerLazyOptional.ifPresent(chestHandler -> {
            this.addSlot(new SlotItemHandler(chestHandler, NayutaChestHandler.SLOT_INPUT, 44, 35));
            this.addSlot(new SlotItemHandler(chestHandler, NayutaChestHandler.SLOT_OUTPUT, 116, 35));
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
