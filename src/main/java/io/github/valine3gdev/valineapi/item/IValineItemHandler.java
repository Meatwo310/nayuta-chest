package io.github.valine3gdev.valineapi.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IValineItemHandler {
    /**
     * Returns the number of slots available in this inventory.
     * Note that this includes input-only slots.
     *
     * <p>See also: {@link IItemHandler#getSlots()}</p>
     *
     * @return The number of slots available
     */
    int getSlots();

    /**
     * Returns the ValineItemStack in a given slot.
     *
     * <p>
     * <strong>IMPORTANT:</strong> DO NOT MODIFY THE RETURNED STACK!
     * Use those instead:
     * {@link #insertValineItem(int, ValineItemStack, boolean)},
     * {@link #extractValineItem(int, BigInteger, boolean)},
     * {@link IItemHandlerModifiable#setStackInSlot(int, ItemStack)}
     * </p>
     *
     * <p>See also: {@link IItemHandler#getStackInSlot(int)}</p>
     *
     * @param slot Slot to query
     * @return ValineItemStack in given slot. Empty ValineItemStack if the slot is empty.
     */
    ValineItemStack getValineStackInSlot(int slot);

    /**
     * Inserts a ValineItemStack into the given slot and returns the remaining stack.
     * Given stack should NOT be modified in this method.
     *
     * <p>See also: {@link IItemHandler#insertItem(int, ItemStack, boolean)}</p>
     *
     * @param slot     Slot to insert into
     * @param stack    ValineItemStack to insert.
     *                 This stack must not be modified by this handler.
     * @param simulate If true, the insertion is only simulated and no changes are made.
     * @return The remaining stack that was not inserted.
     */
    ValineItemStack insertValineItem(int slot, ValineItemStack stack, boolean simulate);

    /**
     * Extracts a ValineItemStack from the given slot.
     *
     * <p>See also: {@link IItemHandler#extractItem(int, int, boolean)}</p>
     *
     * @param slot     Slot to extract from
     * @param amount   Maximum amount to extract.
     * @param simulate If true, the extraction is only simulated and no changes are made.
     * @return The extracted ValineItemStack.
     *         Empty ValineItemStack if nothing can be extracted.
     */
    ValineItemStack extractValineItem(int slot, BigInteger amount, boolean simulate);

    /**
     * Returns the maximum amount of items that can be stored in the given slot.
     *
     * <p>See also: {@link IItemHandlerModifiable#getSlotLimit(int)}</p>
     *
     * @param slot Slot to query
     * @return The maximum amount of items that can be stored in the given slot.
     */
    BigInteger getValineSlotLimit(int slot);

    /**
     * Returns if the given stack is valid for the given slot.
     * This method does NOT check the inventory state.
     *
     * <p>See also: {@link IItemHandler#isItemValid(int, ItemStack)}</p>
     *
     * @param slot  Slot to check
     * @param stack Stack to check
     * @return true if the stack is valid for the given slot and may be inserted,
     *         false if the stack can never be inserted into the slot.
     */
    boolean isValineItemValid(int slot, ValineItemStack stack);
}
