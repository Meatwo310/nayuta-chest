package io.github.meatwo310.nayutachest.handler;

import io.github.meatwo310.nayutachest.util.BigIntegerUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class NayutaChestHandler extends ItemStackHandler {
    public static final BigInteger STACK_LIMIT = BigInteger.valueOf(10).pow(60);
    public static final int FAKE_STACK_LIMIT = Integer.MAX_VALUE;

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;

    BigInteger stackCount = BigInteger.ZERO;

    public NayutaChestHandler() {
        super(2);
    }

    public BigInteger getStackCount() {
        return stackCount;
    }

    public void setStackCount(BigInteger count) {
        if (this.stacks.isEmpty() || this.stacks.get(SLOT_OUTPUT).isEmpty()) return;
        this.getStackInSlot(SLOT_OUTPUT).setCount(BigIntegerUtil.asIntOr(count, FAKE_STACK_LIMIT));
        this.stackCount = count;
    }

    public void setStack(ItemStack stack, BigInteger count) {
        this.stacks.set(SLOT_OUTPUT, stack.copyWithCount(BigIntegerUtil.asIntOr(count, FAKE_STACK_LIMIT)));
        this.stackCount = count;
    }

    protected NonNullList<ItemStack> getStacks() {
        return this.stacks;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        this.validateSlotIndex(slot);

        if (slot == SLOT_INPUT)
            return ItemStack.EMPTY;

        return new ItemStack(
                this.stacks.get(slot).getItem(),
                BigIntegerUtil.asIntOr(this.stackCount, FAKE_STACK_LIMIT)
        );
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        // the stack is empty
        if (stack.isEmpty()) return ItemStack.EMPTY;
        // the stack is not valid for this slot
        if (!this.isItemValid(SLOT_OUTPUT, stack)) return stack;

        // throw an error if the slot index is out of bounds
        this.validateSlotIndex(slot);

        // get the existing stack in the slot and the remaining space in the slot
        ItemStack existingStack = this.stacks.get(SLOT_OUTPUT);
        BigInteger remainingSpace;
        if (existingStack.isEmpty()) {
            remainingSpace = STACK_LIMIT;
        } else {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existingStack)) return stack;
            remainingSpace = STACK_LIMIT.subtract(stackCount);
        }

        // this handler is already full
        if (remainingSpace.compareTo(BigInteger.ZERO) <= 0) return stack;

        // check if the stack will be fill up the slot
        boolean willReachLimit = remainingSpace.compareTo(BigInteger.valueOf(stack.getCount())) < 0;
        if (!simulate) {
            // set the stack or update the stack count in the slot
            if (existingStack.isEmpty()) {
                this.setStack(stack, willReachLimit ? STACK_LIMIT : BigInteger.valueOf(stack.getCount()));
            } else {
                this.setStackCount(willReachLimit ? remainingSpace : this.stackCount.add(BigInteger.valueOf(stack.getCount())));
            }
            this.onContentsChanged(SLOT_OUTPUT);
        }

        // all items were accepted
        if (!willReachLimit) return ItemStack.EMPTY;

        // some items were not accepted so return the remainder
        int remainderCount = stack.getCount() - BigIntegerUtil.asIntOr(remainingSpace, stack.getCount());
        return ItemHandlerHelper.copyStackWithSize(stack, remainderCount);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;
        this.validateSlotIndex(slot);

        ItemStack existingStack = this.stacks.get(SLOT_OUTPUT);
        if (existingStack.isEmpty()) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, BigIntegerUtil.asIntOr(this.stackCount, FAKE_STACK_LIMIT));

        // the slot has enough items to extract
        if (this.stackCount.compareTo(BigInteger.valueOf(toExtract)) > 0) {
            if (!simulate) {
                this.setStackCount(stackCount.subtract(BigInteger.valueOf(toExtract)));
                this.onContentsChanged(SLOT_OUTPUT);
            }
            return ItemHandlerHelper.copyStackWithSize(existingStack, toExtract);
        }

        // the slot does not have enough items to extract
        if (simulate) return existingStack.copy();
        this.setStack(ItemStack.EMPTY, BigInteger.ZERO);
        this.onContentsChanged(SLOT_OUTPUT);
        return existingStack;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag itemTag = new CompoundTag();
        this.stacks.get(SLOT_OUTPUT).save(itemTag);
        itemTag.putByte("Count", (byte) 1);

        CompoundTag nbt = new CompoundTag();
        nbt.put("Stack", itemTag);
        nbt.putString("Count", this.stackCount.toString());

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ItemStack stack = nbt.contains("Stack") ? ItemStack.of(nbt.getCompound("Stack")) : ItemStack.EMPTY;
        BigInteger count = nbt.contains("Count") ? new BigInteger(nbt.getString("Count")) : BigInteger.ZERO;
        this.setStack(stack, count);
    }
}
