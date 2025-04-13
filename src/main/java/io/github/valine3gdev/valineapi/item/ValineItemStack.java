package io.github.valine3gdev.valineapi.item;

import com.mojang.logging.LogUtils;
import io.github.valine3gdev.valineapi.util.BigIntegerUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;

/**
 * Represents an item stack with a BigInteger count instead of the vanilla's int count.
 * This allows for handling extremely large quantities of items.
 *
 * @param stack The ItemStack (always stored with count=1)
 * @param count The BigInteger count representing the quantity
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record ValineItemStack(ItemStack stack, BigInteger count) {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ValineItemStack EMPTY = new ValineItemStack(ItemStack.EMPTY, BigInteger.ZERO);

    /**
     * Creates a new ValineItemStack from a regular Minecraft ItemStack.
     * This constructor extracts the count from the provided ItemStack and converts it to a BigInteger.
     * The original ItemStack will be stored with a count of 1, while the actual quantity is preserved
     * in the BigInteger count.
     *
     * @param stack The ItemStack whose count will be converted to a BigInteger
     */
    public ValineItemStack(ItemStack stack) {
        this(stack, BigInteger.valueOf(stack.getCount()));
    }

    /**
     * Creates a new ValineItemStack with the given ItemStack and count.
     * The ItemStack is always stored with a count of 1 to prevent duplication
     * of vanilla count and our BigInteger count.
     *
     * @param stack The ItemStack to store
     * @param count The BigInteger count
     */
    public ValineItemStack(ItemStack stack, BigInteger count) {
        this.stack = stack.copyWithCount(1);
        this.count = count;
    }

    /**
     * Creates a copy of this ValineItemStack.
     *
     * @return A new ValineItemStack with the same stack and count
     */
    public ValineItemStack copy() {
        return new ValineItemStack(this.stack, this.count);
    }

    /**
     * Creates a copy of this ValineItemStack with a different count.
     *
     * @param count The new BigInteger count
     * @return A new ValineItemStack with the same stack but different count
     */
    public ValineItemStack copyWithCount(BigInteger count) {
        return new ValineItemStack(this.stack, count);
    }

    /**
     * Checks if this ValineItemStack is empty.
     * An item stack is considered empty if either the stack itself is empty
     * or the count is zero.
     *
     * @return true if the stack is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.stack.isEmpty() || this.count.equals(BigInteger.ZERO);
    }

    /**
     * Creates a new ValineItemStack with an increased count.
     *
     * @param amount The amount to add to the count
     * @return A new ValineItemStack with the increased count
     */
    public ValineItemStack growCopy(BigInteger amount) {
        return new ValineItemStack(this.stack, this.count.add(amount));
    }

    /**
     * Creates a new ValineItemStack with a decreased count.
     *
     * @param amount The amount to subtract from the count
     * @return A new ValineItemStack with the decreased count
     */
    public ValineItemStack shrinkCopy(BigInteger amount) {
        return new ValineItemStack(this.stack, this.count.subtract(amount));
    }

    /**
     * Converts this ValineItemStack to a regular Minecraft ItemStack.
     * The count is capped at Integer.MAX_VALUE if it exceeds that value.
     *
     * @return A new ItemStack with the appropriate count
     */
    public ItemStack toItemStack() {
        return this.stack().copyWithCount(BigIntegerUtil.asInt(this.count()));
    }

    public ItemStack toItemStack(int max) {
        return this.stack().copyWithCount(BigIntegerUtil.asInt(this.count(), max));
    }

    /**
     * Saves this ValineItemStack to a CompoundTag.
     * Stores both the ItemStack data and the BigInteger count.
     *
     * @param tag The CompoundTag to save to
     * @return The CompoundTag with the saved data
     */
    public CompoundTag save(CompoundTag tag) {
        ItemStack stack = this.stack();
        stack.save(tag);
        tag.putString("vCount", this.count().toString());
        return tag;
    }

    public String toString() {
        return this.count() + " " + this.stack.getItem();
    }
}
