package io.github.meatwo310.nayutachest.handler;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class NayutaChestDisplayHandler extends ItemStackHandler {
    private NayutaChestHandler nayutaChestHandler;

    public NayutaChestDisplayHandler(NayutaChestHandler nayutaChestHandler) {
        super(1);
        this.setHandler(nayutaChestHandler);
    }

    public void setHandler(NayutaChestHandler nayutaChestHandler) {
        this.nayutaChestHandler = nayutaChestHandler;
        this.stacks = nayutaChestHandler.getStacks();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        this.validateSlotIndex(slot);
        return nayutaChestHandler
                .getStackInSlot(NayutaChestHandler.SLOT_OUTPUT)
                .copyWithCount(1);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }
}
