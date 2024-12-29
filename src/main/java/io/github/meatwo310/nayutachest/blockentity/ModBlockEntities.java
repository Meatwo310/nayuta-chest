package io.github.meatwo310.nayutachest.blockentity;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NayutaChest.MODID);

    public static final RegistryObject<BlockEntityType<NayutaChestBE>> NAYUTA_CHEST =
            TILE_ENTITY_TYPES.register(
                    ModBlocks.NAYUTA_CHEST.getId().getPath(),
                    () -> BlockEntityType.Builder.of(
                            NayutaChestBE::new,
                            ModBlocks.NAYUTA_CHEST.get()
                    ).build(null)
            );

    public static void register(IEventBus modEventBus) {
        TILE_ENTITY_TYPES.register(modEventBus);
    }
}
