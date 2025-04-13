package io.github.valine3gdev.valineapi.item;

import com.mojang.logging.LogUtils;
import io.github.valine3gdev.valineapi.util.BigIntegerUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ValineItemStackHandler extends ItemStackHandler implements IValineItemHandler, IValineItemHandlerModifiable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int FAKE_STACK_LIMIT_DEFAULT = Integer.MAX_VALUE;
    public static final BigInteger SLOT_LIMIT_DEFAULT = BigIntegerUtil.NOVEMDECILLION;
    public static final int SLOT_INPUT = 0;

    protected NonNullList<ValineItemStack> stacks;

    public ValineItemStackHandler() {
        this(2);
    }

    public ValineItemStackHandler(int size) {
        if (size < 2) throw new IllegalArgumentException("Size must be at least 2");
        this.stacks = NonNullList.withSize(size, ValineItemStack.EMPTY);
        super.stacks = NonNullList.create();
    }

    public ValineItemStackHandler(NonNullList<ValineItemStack> stacks) {
        this.stacks = stacks;
        super.stacks = NonNullList.create();
    }

    @Override
    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.stacks.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
        }
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot > SLOT_INPUT && slot < stacks.size()) {
                if (!itemTags.contains("vCount")) continue;

                stacks.set(slot, new ValineItemStack(ItemStack.of(itemTags))
                        .copyWithCount(new BigInteger(itemTags.getString("vCount")))
                );
            }
        }
        onLoad();
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = SLOT_INPUT + 1; i < this.stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stacks.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", this.stacks.size());
        return nbt;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return this.getSlotLimit(slot);
    }

    @Override
    public int getSlotLimit(int slot) {
        return FAKE_STACK_LIMIT_DEFAULT;
    }

    @Override
    public BigInteger getValineSlotLimit(int slot) {
        return SLOT_LIMIT_DEFAULT;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extractValineItem(slot, BigInteger.valueOf(amount), simulate).toItemStack();
    }

    @Override
    public ValineItemStack extractValineItem(int slot, BigInteger amount, boolean simulate) {
        if (amount.signum() <= 0) return ValineItemStack.EMPTY;
        if (slot == SLOT_INPUT) return ValineItemStack.EMPTY;
        this.validateSlotIndex(slot);

        ValineItemStack existing = this.stacks.get(slot);
        if (existing.isEmpty()) return ValineItemStack.EMPTY;

        BigInteger toExtract = existing.count().min(amount);

        if (existing.count().compareTo(toExtract) <= 0) {
            if (!simulate) {
                this.stacks.set(slot, ValineItemStack.EMPTY);
                onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.stacks.set(slot, ValineItemHandlerHelper.copyStackWithSize(existing, existing.count().subtract(toExtract)));
                onContentsChanged(slot);
            }

            return ValineItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.insertValineItem(slot, new ValineItemStack(stack), simulate).toItemStack();
    }

    @Override
    public ValineItemStack insertValineItem(int slot, ValineItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return stack;
        if (!this.isValineItemValid(slot, stack)) return stack;
        if (slot == SLOT_INPUT) return stack;

        validateSlotIndex(slot);

        ValineItemStack existing = this.stacks.get(slot);
        BigInteger limit = this.getValineSlotLimit(slot);

        if (!existing.isEmpty()) {
            if (!ValineItemHandlerHelper.canValineItemStacksStack(stack, existing))
                return stack;

            limit = limit.subtract(existing.count());
        }

        if (limit.signum() <= 0) return stack;

        boolean reachedLimit = stack.count().compareTo(limit) > 0;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? ValineItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                this.stacks.set(slot, existing.growCopy(reachedLimit ? limit : stack.count()));
            }
            this.onContentsChanged(slot);
        }

        return reachedLimit ? ValineItemHandlerHelper.copyStackWithSize(stack, stack.count().subtract(limit)) : ValineItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        ValineItemStack valineStack = this.getValineStackInSlot(slot);
        return valineStack.toItemStack(this.getStackLimit(slot, valineStack.stack()));
    }

    @Override
    public ValineItemStack getValineStackInSlot(int slot) {
        this.validateSlotIndex(slot);
        return slot <= SLOT_INPUT ? ValineItemStack.EMPTY : stacks.get(slot);
    }

    @Override
    public int getSlots() {
        return this.stacks.size();
    }

    @Override
    public void setValineStackInSlot(int slot, ValineItemStack stack) {
        validateSlotIndex(slot);
        if (slot == SLOT_INPUT) {
            ValineItemStack remaining = ValineItemHandlerHelper.insertItem(this, stack, false);
            if (!remaining.isEmpty()) {
                LOGGER.error("Failed to redirect setValineStackInSlot to insertItem. Remaining stack: {}", remaining);
            }
        } else {
            this.stacks.set(slot, stack);
        }
        onContentsChanged(slot);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.setValineStackInSlot(slot, new ValineItemStack(stack));
    }

    @Override
    public void setSize(int size) {
        this.stacks = NonNullList.withSize(size, ValineItemStack.EMPTY);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.isValineItemValid(slot, new ValineItemStack(stack));
    }

    @Override
    public boolean isValineItemValid(int slot, ValineItemStack stack) {
        return true;
    }
}
