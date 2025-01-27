package io.github.meatwo310.nayutachest;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.nayutachest.block.ModBlocks;
import io.github.meatwo310.nayutachest.blockentity.ModBlockEntities;
import io.github.meatwo310.nayutachest.config.ClientConfig;
import io.github.meatwo310.nayutachest.config.CommonConfig;
import io.github.meatwo310.nayutachest.config.ServerConfig;
import io.github.meatwo310.nayutachest.item.ModCreativeModeTabs;
import io.github.meatwo310.nayutachest.item.ModItems;
import io.github.meatwo310.nayutachest.menu.ModMenus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(NayutaChest.MODID)
public class NayutaChest {
    public static final String MODID = "nayutachest";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NayutaChest() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModMenus.register(modEventBus);
//        ModRecipes.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }
}
