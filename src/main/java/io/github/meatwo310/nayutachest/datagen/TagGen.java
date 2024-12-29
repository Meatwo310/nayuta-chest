package io.github.meatwo310.nayutachest.datagen;

import io.github.meatwo310.nayutachest.NayutaChest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TagGen {
    private static final List<RegistryObject<Block>> mineableWithPickaxe = new ArrayList<>();

    protected static void register(boolean run, DataGenerator generator, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper efh) {
        BlockTagGen blockTagGen = generator.addProvider(run, new BlockTagGen(output, lookupProvider, efh));
        generator.addProvider(run, new ItemTagGen(output, lookupProvider, blockTagGen.contentsGetter(), efh));
    }

    public static void addMineableWithPickaxeBlock(RegistryObject<Block> block) {
        mineableWithPickaxe.add(block);
    }

    public static class BlockTagGen extends BlockTagsProvider {
        public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper efh) {
            super(output, lookupProvider, NayutaChest.MODID, efh);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            mineableWithPickaxe.forEach(block -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get()));
        }
    }

    public static class ItemTagGen extends ItemTagsProvider {
        public ItemTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> lookupBlock, @Nullable ExistingFileHelper efh) {
            super(output, lookupProvider, lookupBlock, NayutaChest.MODID, efh);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
        }
    }
}
