package io.github.valine3gdev.valineapi.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IValineItemHandlerModifiable {
    /**
     * Overrides the stack in the given slot.
     * It is not intended for use outside of the Valine API helper.
     * See also: {@link IItemHandlerModifiable#setStackInSlot(int, ItemStack)}
     *
     * @param slot  Slot to modify
     * @param stack ValineItemStack to set
     * @throws RuntimeException if the handler is called in an unexpected way
     */
    void setValineStackInSlot(int slot, ValineItemStack stack);
}
