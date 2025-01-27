package io.github.meatwo310.nayutachest.handler;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.nayutachest.config.ServerConfig;
import io.github.meatwo310.nayutachest.util.BigIntegerUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.math.BigInteger;

public class NayutaChestHandler extends ItemStackHandler {
    public static final Logger LOGGER = LogUtils.getLogger();

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
        if (count.compareTo(BigInteger.ZERO) <= 0) {
            this.setStack(ItemStack.EMPTY, BigInteger.ZERO);
        } else {
            this.getStackInSlot(SLOT_OUTPUT).setCount(BigIntegerUtil.asIntOr(count, FAKE_STACK_LIMIT));
            this.stackCount = count;
        }
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
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.validateSlotIndex(slot);
        ItemStack result = this.insertItem(slot, stack, false);
        if (!result.isEmpty()) {
            LOGGER.error("Failed to insert stack {} into slot {}. Remaining stack: {}", stack, slot, result);
        }
        this.onContentsChanged(SLOT_OUTPUT);
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
            remainingSpace = ServerConfig.storageSizeCache;
        } else {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existingStack)) return stack;
            remainingSpace = ServerConfig.storageSizeCache.subtract(stackCount);
        }

        // this handler is already full
        if (remainingSpace.compareTo(BigInteger.ZERO) <= 0) return stack;

        // check if the stack will be fill up the slot
        boolean willReachLimit = remainingSpace.compareTo(BigInteger.valueOf(stack.getCount())) < 0;
        if (!simulate) {
            // set the stack or update the stack count in the slot
            if (existingStack.isEmpty()) {
                this.setStack(stack, willReachLimit ? ServerConfig.storageSizeCache : BigInteger.valueOf(stack.getCount()));
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
        if (this.stackCount.compareTo(BigInteger.valueOf(toExtract)) >= 0) {
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
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return this.getSlotLimit(slot);
    }

    @Override
    public CompoundTag serializeNBT() {
        if (this.stacks.isEmpty() || this.stacks.get(SLOT_OUTPUT).isEmpty())
            return new CompoundTag();

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
