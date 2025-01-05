package io.github.meatwo310.nayutachest.handler;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class NayutaChestDisplayHandler extends ItemStackHandler {
    private NayutaChestHandler nayutaChestHandler;

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_DISPLAY = 2;

    public NayutaChestDisplayHandler(NayutaChestHandler nayutaChestHandler) {
        super(3);
        this.setHandler(nayutaChestHandler);
    }

    public void setHandler(NayutaChestHandler nayutaChestHandler) {
        this.nayutaChestHandler = nayutaChestHandler;
        this.stacks = NonNullList.withSize(3, ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        this.validateSlotIndex(slot);
        return switch (slot) {
            case SLOT_INPUT, SLOT_OUTPUT -> {
                ItemStack stackInSlot = this.nayutaChestHandler
                        .getStackInSlot(slot);
                yield stackInSlot.copyWithCount(Math.min(stackInSlot.getCount(), this.getSlotLimit(slot)));
            }
            case SLOT_DISPLAY -> this.nayutaChestHandler
                    .getStackInSlot(NayutaChestHandler.SLOT_OUTPUT)
                    .copyWithCount(1);
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        // no slot validation needed since NayutaChestHandler will handle it
        if (slot == SLOT_DISPLAY) {
            return stack;
        }
        return this.nayutaChestHandler.insertItem(NayutaChestHandler.SLOT_INPUT, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        // no slot validation needed since NayutaChestHandler will handle it
        if (slot == SLOT_DISPLAY) {
            return ItemStack.EMPTY;
        }
        return this.nayutaChestHandler.extractItem(NayutaChestHandler.SLOT_OUTPUT, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return switch (slot) {
            case SLOT_INPUT, SLOT_OUTPUT -> 64;
            case SLOT_DISPLAY -> 1;
            default -> 0;
        };
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return this.getSlotLimit(slot);
    }
}
