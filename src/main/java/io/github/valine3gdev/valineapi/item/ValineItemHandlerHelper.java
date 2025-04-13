package io.github.valine3gdev.valineapi.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Helper class for ValineItemHandler operations.
 * Provides utility methods for item insertion and stack comparison.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ValineItemHandlerHelper {
    /**
     * Inserts an item into the first valid slot of the destination inventory.
     *
     * @param dest     The destination inventory to insert into
     * @param stack    The ValineItemStack to insert
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ValineItemStack that was not inserted (if the stack is empty, the insertion was successful)
     */
    public static ValineItemStack insertItem(IValineItemHandler dest, ValineItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return stack;

        for (int i = ValineItemStackHandler.SLOT_INPUT + 1; i < dest.getSlots(); i++) {
            stack = dest.insertValineItem(i, stack, simulate);
            if (stack.isEmpty()) break;
        }

        return stack;
    }

    /**
     * Inserts an item into the destination inventory with stacking priority.
     * First attempts to stack with existing items, then tries to insert into empty slots.
     *
     * @param inventory The destination inventory to insert into
     * @param stack     The ValineItemStack to insert
     * @param simulate  If true, the insertion is only simulated
     * @return The remaining ValineItemStack that was not inserted (if the stack is empty, the insertion was successful)
     */
    public static ValineItemStack insertItemStacked(IValineItemHandler inventory, ValineItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return stack;

        int sizeInventory = inventory.getSlots();

        for (int i = ValineItemStackHandler.SLOT_INPUT + 1; i < sizeInventory; i++) {
            ValineItemStack slot = inventory.getValineStackInSlot(i);
            if (canValineItemStacksStack(slot, stack)) {
                stack = inventory.insertValineItem(i, stack, simulate);
                if (stack.isEmpty()) break;
            }
        }

        if (!stack.isEmpty()) {
            for (int i = ValineItemStackHandler.SLOT_INPUT + 1; i < sizeInventory; i++) {
                if (inventory.getValineStackInSlot(i).isEmpty()) {
                    stack = inventory.insertValineItem(i, stack, simulate);
                    if (stack.isEmpty()) break;
                }
            }
        }

        return stack;
    }

    /**
     * Determines if two ValineItemStacks can stack together.
     * Items can stack if they are the same item, have compatible tags, and compatible capabilities.
     *
     * @param a The first ValineItemStack
     * @param b The second ValineItemStack
     * @return True if the stacks can be stacked together, false otherwise
     */
    public static boolean canValineItemStacksStack(ValineItemStack a, ValineItemStack b) {
        ItemStack aStack = a.stack();
        ItemStack bStack = b.stack();

        if (a.isEmpty()) return false;
        if (!ItemStack.isSameItem(aStack, bStack)) return false;
        if (aStack.hasTag() != bStack.hasTag()) return false;

        return (!aStack.hasTag() || Objects.equals(aStack.getTag(), bStack.getTag())) &&
                aStack.areCapsCompatible(bStack);
    }

    /**
     * Creates a copy of the given ValineItemStack with a specified size.
     *
     * @param valineStack The ValineItemStack to copy
     * @param size        The new stack size (as BigInteger)
     * @return A new ValineItemStack with the specified size, or EMPTY if size is zero
     */
    public static ValineItemStack copyStackWithSize(ValineItemStack valineStack, BigInteger size) {
        if (size.equals(BigInteger.ZERO)) return ValineItemStack.EMPTY;
        return valineStack.copyWithCount(size);
    }
}