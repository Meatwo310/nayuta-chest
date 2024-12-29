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
    public static <T extends ItemStackHandler> void load(CompoundTag nbt, String key, LazyOptional<T> lazyOptionalHandler) {
        if (!nbt.contains(key)) {
            LOGGER.warn("NBT does not contain key: {}", key);
            return;
        }
        lazyOptionalHandler.ifPresent(handler -> {
            LOGGER.warn("Loading ItemStackHandler: {}", nbt.getCompound(key));
            handler.deserializeNBT(nbt.getCompound(key));
            LOGGER.warn("Loaded ItemStackHandler[0]: {}", handler.getStackInSlot(0));
        });
    }

    public static <T extends ItemStackHandler> void saveAdditional(CompoundTag nbt, String key, LazyOptional<T> lazyOptionalHandler) {
        lazyOptionalHandler.ifPresent(handler -> nbt.put(key, handler.serializeNBT()));
    }
}
