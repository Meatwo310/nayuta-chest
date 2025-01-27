package io.github.meatwo310.nayutachest.config;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.nayutachest.NayutaChest;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

import java.math.BigInteger;

@Mod.EventBusSubscriber(modid = NayutaChest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BigInteger DEFAULT_STORAGE_SIZE = BigInteger.valueOf(10).pow(60);
    
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<? extends String> STORAGE_SIZE = BUILDER
            .comment("""
                    The size of the Nayuta Chest in positive integer.
                    Larger values may cause performance and network issues.""")
            .define(
                    "storageSize",
                    DEFAULT_STORAGE_SIZE.toString(),
                    ServerConfig::isValidStorageSize
            );

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static BigInteger storageSizeCache = DEFAULT_STORAGE_SIZE;

    private static boolean isValidStorageSize(Object obj) {
        if (obj instanceof String size) {
            try {
                new java.math.BigInteger(size);
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onModConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            storageSizeCache = new BigInteger(STORAGE_SIZE.get());
            LOGGER.info("Storage size updated to {}", storageSizeCache);
        }
    }
}
