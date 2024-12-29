package io.github.meatwo310.nayutachest.block;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.datagen.ModelGen;
import io.github.meatwo310.nayutachest.datagen.TagGen;
import io.github.meatwo310.nayutachest.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NayutaChest.MODID);
    public static final Map<String, RegistryObject<Block>> BLOCKS_MAP = new LinkedHashMap<>();

    public static final RegistryObject<Block> NAYUTA_CHEST = add(
            "nayutachest",
            () -> new NayutaChestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(3.0F, 1200.0F)
                    .sound(SoundType.WOOD)
            ),
            () -> new BlockItem(ModBlocks.NAYUTA_CHEST.get(), new BlockItem.Properties())
    );

    private static RegistryObject<Block> add(String name, Supplier<Block> blockSupplier, Supplier<BlockItem> blockItemSupplier) {
        RegistryObject<Block> block = BLOCKS.register(name, blockSupplier);
        BLOCKS_MAP.put(name, block);
        ModItems.addBlockItem(name, blockItemSupplier);
        ModelGen.addBasicBlock(block);
        TagGen.addMineableWithPickaxeBlock(block);
        return block;
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
