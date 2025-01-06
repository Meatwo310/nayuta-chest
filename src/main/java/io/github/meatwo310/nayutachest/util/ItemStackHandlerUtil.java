package io.github.meatwo310.nayutachest.util;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemStackHandlerUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Load the {@code LazyOptional<T extends ItemStackHandler>} from the NBT with the given key.
     * @param nbt the NBT to load from
     * @param key the key to load the handler from
     * @param lazyOptionalHandler the LazyOptional Handler to load from the NBT
     * @param <T> the type of the handler
     */
    public static <T extends ItemStackHandler> void load(CompoundTag nbt, String key, LazyOptional<T> lazyOptionalHandler) {
        if (!nbt.contains(key)) {
            LOGGER.warn("NBT does not contain key: {}", key);
            return;
        }
        lazyOptionalHandler.ifPresent(handler -> {
            handler.deserializeNBT(nbt.getCompound(key));
        });
    }

    /**
     * Save the {@code LazyOptional<ItemStackHandler>} to the NBT with the given key.
     * Does nothing if the handler is not present.
     * @param nbt the NBT to save to
     * @param key the key to save the handler to
     * @param lazyOptionalHandler the LazyOptional Handler to save to the NBT
     * @param <T> the type of the handler
     */
    public static <T extends ItemStackHandler> void saveAdditional(CompoundTag nbt, String key, LazyOptional<T> lazyOptionalHandler) {
        lazyOptionalHandler.ifPresent(handler -> nbt.put(key, handler.serializeNBT()));
    }

    /**
     * Returns true if the {@code LazyOptional<ItemStackHandler>} is empty or not present.
     * @see ItemStackHandlerUtil#isEmpty(LazyOptional, boolean)
     */
    public static <T extends ItemStackHandler> boolean isEmpty(LazyOptional<T> lazyOptionalHandler) {
        return isEmpty(lazyOptionalHandler, true);
    }

    /**
     * Check if the {@code LazyOptional<ItemStackHandler>} is empty.
     * @param lazyOptionalHandler the LazyOptional Handler to check
     * @param defaultValue the value to return if the LazyOptional is not present
     * @return true if the handler is empty, {@code defaultValue} if the handler is not present, false otherwise
     * @param <T> the type of the handler
     */
    public static <T extends ItemStackHandler> boolean isEmpty(LazyOptional<T> lazyOptionalHandler, boolean defaultValue) {
        return lazyOptionalHandler.map(handler -> {
            int slots = handler.getSlots();
            for (int i = 0; i < slots; i++) {
                if (!handler.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }).orElse(defaultValue);
    }
}
