package io.github.valine3gdev.valineapi.item;

import com.mojang.logging.LogUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ValineDisplayHandler extends ValineItemStackHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ValineItemStackHandler handler;

    public ValineDisplayHandler(ValineItemStackHandler handler) {
        super(handler.getSlots());
        this.setHandler(handler);
    }

    public void setHandler(ValineItemStackHandler valineItemStackHandler) {
        this.handler = valineItemStackHandler;
        this.stacks = NonNullList.withSize(valineItemStackHandler.getSlots(), ValineItemStack.EMPTY);
    }

    @Override
    public ValineItemStack getValineStackInSlot(int slot) {
        return this.handler.getValineStackInSlot(slot);
    }

    @Override
    public void setValineStackInSlot(int slot, ValineItemStack stack) {
        if (slot != SLOT_INPUT) return;
        ValineItemHandlerHelper.insertItem(this.handler, stack, false);
    }

    @Override
    public ValineItemStack insertValineItem(int slot, ValineItemStack stack, boolean simulate) {
        if (slot != SLOT_INPUT) return stack;
        return ValineItemHandlerHelper.insertItem(this.handler, stack, simulate);
    }

    @Override
    public ValineItemStack extractValineItem(int slot, BigInteger amount, boolean simulate) {
        if (slot == SLOT_INPUT) return ValineItemStack.EMPTY;
        return this.handler.extractValineItem(slot, amount, simulate);
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public BigInteger getValineSlotLimit(int slot) {
        return super.getValineSlotLimit(slot);
    }
}
