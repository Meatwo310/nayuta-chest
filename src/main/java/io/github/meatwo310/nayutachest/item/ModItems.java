package io.github.meatwo310.nayutachest.item;

import io.github.meatwo310.nayutachest.NayutaChest;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NayutaChest.MODID);
    public static final Map<String, RegistryObject<Item>> ITEM_MAP = new LinkedHashMap<>();

    public static void addBlockItem(String name, Supplier<BlockItem> blockItemSupplier) {
        ITEM_MAP.put(name, ITEMS.register(name, blockItemSupplier));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
