package io.github.meatwo310.nayutachest.menu;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.nayutachest.block.ModBlocks;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.handler.NayutaChestDisplayHandler;
import io.github.meatwo310.nayutachest.handler.NayutaChestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Arrays;

public class NayutaChestMenu extends AbstractContainerMenu {
    private static final Logger LOGGER = LogUtils.getLogger();

    public final NayutaChestBE nayutaChestBlock;
    private final ContainerLevelAccess containerLevelAccess;
    public final ContainerData containerData;
    //    private final ContainerData containerData;

    private final int INPUT_SLOT;
    private final int OUTPUT_SLOT;
    private final int DISPLAY_SLOT;

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
        int[] beSlots = createBESlots(be);
        INPUT_SLOT = beSlots[0];
        OUTPUT_SLOT = beSlots[1];
        DISPLAY_SLOT = beSlots[2];

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

    private int[] createBESlots(NayutaChestBE blockEntity) {
        int[] slotIndexes = new int[3];
        Arrays.fill(slotIndexes, -1);

        blockEntity.displayHandlerLazyOptional.ifPresent(displayHandler -> {
            Slot input = this.addSlot(new SlotItemHandler(
                    displayHandler,
                    NayutaChestDisplayHandler.SLOT_INPUT,
                    35,
                    35
            ));
            Slot output = this.addSlot(new SlotItemHandler(
                    displayHandler,
                    NayutaChestDisplayHandler.SLOT_OUTPUT,
                    125,
                    35
            ));
            Slot display = this.addSlot(new SlotItemHandler(
                    displayHandler,
                    NayutaChestDisplayHandler.SLOT_DISPLAY,
                    80,
                    35
            ));
            slotIndexes[0] = input.index;
            slotIndexes[1] = output.index;
            slotIndexes[2] = display.index;

            StringBuilder stackLog = new StringBuilder();
            for (int i = 0; i < displayHandler.getSlots(); i++) {
                stackLog.append(String.format("stack[%d]:%s, ", i, displayHandler.getStackInSlot(i)));
            }
            LOGGER.debug(stackLog.toString());

            nayutaChestBlock.chestHandlerLazyOptional.ifPresent(chestHandler -> {
                StringBuilder beStackLog = new StringBuilder();
                for (int i = 0; i < chestHandler.getSlots(); i++) {
                    beStackLog.append(String.format("beStack[%d]:%s, ", i, chestHandler.getStackInSlot(i)));
                }
                LOGGER.debug(beStackLog.toString());
            });
        });

        return slotIndexes;
    }

    @Override
    @NotNull
    public ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        Slot slot = this.getSlot(slotIndex);
        ItemStack itemStack = slot.getItem();

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStackCopy = itemStack.copy();

        boolean success;
        if (slotIndex < Inventory.INVENTORY_SIZE) {
            success = this.movePlayerToContainer(player, slotIndex, itemStack);
        } else {
            success = this.moveContainerToPlayer(player, slotIndex, itemStack);
//            return ItemStack.EMPTY;
        }
        if (!success) {
            return ItemStack.EMPTY;
        }

        slot.setChanged();
        slot.onTake(player, itemStack);

        return itemStackCopy;
    }

    public boolean movePlayerToContainer(Player player, int slotIndex, ItemStack itemStack) {
        // Move inventory item (#0-35) to BE slot (#36)
        return this.moveBetweenInventoryAndBE(
                itemStack,
                36,
                37,
                false
        );
    }

    public boolean moveContainerToPlayer(Player player, int slotIndex, ItemStack itemStack) {
        // Prevent moving display slot (#38) to inventory
        if (slotIndex >= DISPLAY_SLOT) return false;

        // Move BE item (#36-38) to inventory slot (#0-35)
        return this.moveBetweenInventoryAndBE(
                itemStack,
                0,
                Inventory.INVENTORY_SIZE,
                false
        );
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.containerLevelAccess, player, ModBlocks.NAYUTA_CHEST.get());
    }

    private boolean moveBetweenInventoryAndBE(@NotNull ItemStack stack, int start, int size, boolean lastToFirst) {
        boolean moved = false;
        for (int i = (lastToFirst ? size - 1 : start); (lastToFirst ? i >= start : i < size); i += (lastToFirst ? -1 : 1)) {
            if (stack.isEmpty()) {
                break;
            }
            if (!this.canInsert(i, stack)) {
                continue;
            }
            NayutaChestHandler chestHandler = this.nayutaChestBlock.chestHandlerLazyOptional.orElseThrow(() ->
                    new IllegalStateException("chestHandlerLazyOptional is not present")
            );
            ItemStack stackInSlot = chestHandler.getStackInSlot(NayutaChestHandler.SLOT_OUTPUT);
            if (!stackInSlot.isEmpty() && !ItemStack.isSameItemSameTags(stack, stackInSlot)) {
                continue;
            }
            Slot targetSlot = this.slots.get(i);
            ItemStack targetStack = targetSlot.getItem();
            if (i < Inventory.INVENTORY_SIZE) {
                int toExtract = Math.min(stack.getCount(), targetSlot.getMaxStackSize() - targetStack.getCount());
                ItemStack extracted = chestHandler.extractItem(NayutaChestHandler.SLOT_OUTPUT, toExtract, false);
                if (extracted.isEmpty()) {
                    continue;
                }
                stack.shrink(extracted.getCount());
                if (targetStack.isEmpty()) {
                    targetSlot.setByPlayer(extracted);
                } else {
                    targetStack.grow(extracted.getCount());
                }
            } else {
                ItemStack remaining = chestHandler.insertItem(NayutaChestHandler.SLOT_INPUT, stack.copy(), false);
                if (remaining.getCount() >= stack.getCount()) {
                    continue;
                }
                stack.shrink(stack.getCount() - remaining.getCount());
            }
            targetSlot.setChanged();
            moved = true;
        }
        return moved;
    }

    private boolean canInsert(int slotIndex, ItemStack stack) {
        if (slotIndex < 0) {
            return false;
        } else if (slotIndex < Inventory.INVENTORY_SIZE) {
            return canInventoryInsert(slotIndex, stack);
        } else if (slotIndex == INPUT_SLOT) {
            return canBEInsert(slotIndex, stack);
        } else {
            return false;
        }
    }

    private boolean canBEInsert(int slotIndex, ItemStack stack) {
        if (slotIndex != INPUT_SLOT || !this.nayutaChestBlock.chestHandlerLazyOptional.isPresent()) {
            return false;
        }
        NayutaChestHandler chestHandler = this.nayutaChestBlock.chestHandlerLazyOptional.orElseThrow(() ->
                new IllegalStateException("chestHandlerLazyOptional is present but not present")
        );
        ItemStack remaining = chestHandler.insertItem(NayutaChestHandler.SLOT_INPUT, stack, true);
        return remaining.getCount() < stack.getCount();
    }
    
    private boolean canInventoryInsert(int slotIndex, ItemStack stack) {
        if (slotIndex < 0 || slotIndex >= Inventory.INVENTORY_SIZE) {
            return false;
        }
        Slot targetSlot = this.slots.get(slotIndex);
        ItemStack targetStack = targetSlot.getItem();
        return (targetStack.isEmpty() && targetSlot.mayPlace(stack) ||
                ItemStack.isSameItemSameTags(stack, targetStack) && targetStack.getCount() < targetSlot.getMaxStackSize()
        );
    }
}
