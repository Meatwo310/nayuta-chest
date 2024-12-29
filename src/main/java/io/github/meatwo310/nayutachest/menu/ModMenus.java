package io.github.meatwo310.nayutachest.menu;

import io.github.meatwo310.nayutachest.NayutaChest;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, NayutaChest.MODID);

    public static final RegistryObject<MenuType<NayutaChestMenu>> NAYUTA_CHEST_MENU =
            MENUS.register("nayutachest_menu", () -> IForgeMenuType.create(NayutaChestMenu::new));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
