package io.github.meatwo310.nayutachest.menu;

import io.github.meatwo310.nayutachest.block.ModBlocks;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class NayutaChestMenu extends AbstractContainerMenu {
    private final NayutaChestBE nayutaChestBlock;
    private final ContainerLevelAccess containerLevelAccess;
    private final ContainerData containerData;

    // Client
    public NayutaChestMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    // Server
    public NayutaChestMenu(int id, Inventory playerInventory, BlockEntity blockEntity, ContainerData containerData) {
        super(ModMenus.NAYUTA_CHEST_MENU.get(), id);
        if (blockEntity instanceof NayutaChestBE be) {
            this.nayutaChestBlock = be;
        } else {
            throw new IllegalStateException("MachineCoreMenu: BlockEntity is not an instance of MachineCoreTile");
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
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
    }

    private void createBESlots(NayutaChestBE blockEntity) {
//        blockEntity.moduleLazyOptional.ifPresent(inventory ->
//                this.addSlot(new SlotItemHandler(inventory, 0, 8, 18))
//        );
//        blockEntity.upgradeLazyOptional.ifPresent(inventory -> {
//            for (int i = 0; i < 2; i++) {
//                this.addSlot(new SlotItemHandler(inventory, i, 8, 36 + i * 18));
//            }
//            for (int j = 0; j < 3; j++) {
//                this.addSlot(new SlotItemHandler(inventory, j + 2, 152, 18 + j * 18));
//            }
//        });
//        blockEntity.inputLazyOptional.ifPresent(inventory -> {
//            for (int i = 0; i < 4; i++) {
//                this.addSlot(new SlotItemHandler(inventory, i, 35 + (i % 2) * 18, 18 + (i / 2) * 18));
//            }
//        });
//        blockEntity.outputLazyOptional.ifPresent(inventory -> {
//            for (int i = 0; i < 4; i++) {
//                this.addSlot(new SlotItemHandler(inventory, i, 107 + (i % 2) * 18, 18 + (i / 2) * 18));
//            }
//        });
    }

    @Override
    @NotNull
    public ItemStack quickMoveStack(@NotNull Player player, int i) {
        Slot slot = this.getSlot(i);
        ItemStack itemStack = slot.getItem();

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStackCopy = itemStack.copy();

        boolean moveResult;
        if (i < 36) {
            // プレイヤーインベントリのアイテムをブロックエンティティに移動
            moveResult = this.moveItemStackTo(itemStack, 36, this.slots.size(), false);
        } else {
            // ブロックエンティティのアイテムをプレイヤーインベントリに移動
            moveResult = this.moveItemStackTo(itemStack, 0, 36, false);
        }
        if (!moveResult) return ItemStack.EMPTY;

        slot.setChanged();
        slot.onTake(player, itemStack);

        return itemStackCopy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.containerLevelAccess, player, ModBlocks.NAYUTA_CHEST.get());
    }
}
