package io.github.meatwo310.nayutachest.item;

import io.github.meatwo310.nayutachest.block.ModBlocks;
import io.github.meatwo310.nayutachest.NayutaChest;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NayutaChest.MODID);
    public static final String MOD_TAB_ID = "item_group." + NayutaChest.MODID + ".general";
    public static final RegistryObject<CreativeModeTab> MOD_TAB =
            CREATIVE_MODE_TABS.register(MOD_TAB_ID, () -> CreativeModeTab.builder()
                    .title(Component.translatable(MOD_TAB_ID))
                    .icon(() -> ModBlocks.NAYUTA_CHEST.get().asItem().getDefaultInstance())
                    .displayItems((parameters, output) -> ModItems.ITEM_MAP.forEach((name, item) -> {
                        output.accept(item.get());
                    }))
                    .build()
            );

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
