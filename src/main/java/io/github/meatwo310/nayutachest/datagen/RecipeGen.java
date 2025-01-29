package io.github.meatwo310.nayutachest.datagen;

import io.github.meatwo310.nayutachest.block.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RecipeGen {
    protected static void register(boolean run, DataGenerator generator, PackOutput output) {
        generator.addProvider(run, new ModRecipeProvider(output));
    }

    private static class ModRecipeProvider extends RecipeProvider {
        public ModRecipeProvider(PackOutput packOutput) {
            super(packOutput);
        }

        @Override
        protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
            Item nayutaChest = ModBlocks.NAYUTA_CHEST.get().asItem();
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, nayutaChest)
                    .pattern("GGG")
                    .pattern("GCG")
                    .pattern("GDG")
                    .define('G', Tags.Items.GLASS_PANES)
                    .define('C', Tags.Items.CHESTS_WOODEN)
                    .define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
                    .unlockedBy("has_item", has(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                    .save(writer, "nayuta_chest_from_chests");
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, nayutaChest)
                    .pattern("GGG")
                    .pattern("GBG")
                    .pattern("GDG")
                    .define('G', Tags.Items.GLASS_PANES)
                    .define('B', Tags.Items.BARRELS_WOODEN)
                    .define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
                    .unlockedBy("has_item", has(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                    .save(writer, "nayuta_chest_from_barrels");
        }
    }
}
