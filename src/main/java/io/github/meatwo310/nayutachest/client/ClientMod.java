package io.github.meatwo310.nayutachest.client;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.client.screen.NayutaChestMenuScreen;
import io.github.meatwo310.nayutachest.menu.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NayutaChest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientMod {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MenuScreens.register(ModMenus.NAYUTA_CHEST_MENU.get(), NayutaChestMenuScreen::new)
        );
    }
}
