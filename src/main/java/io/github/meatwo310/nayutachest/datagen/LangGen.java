package io.github.meatwo310.nayutachest.datagen;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.block.ModBlocks;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.item.ModCreativeModeTabs;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class LangGen {
    protected static void register(boolean run, DataGenerator generator) {
        generator.addProvider(run, (DataProvider.Factory<EnUs>) EnUs::new);
        generator.addProvider(run, (DataProvider.Factory<JaJp>) JaJp::new);
    }

    private static class EnUs extends LanguageProvider {
        public EnUs(PackOutput output) {
            super(output, NayutaChest.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add(ModCreativeModeTabs.MOD_TAB_ID, "Nayuta Chest");
            add(NayutaChestBE.TITLE_KEY, "Nayuta Chest");
            addBlock(ModBlocks.NAYUTA_CHEST, "Nayuta Chest");
        }
    }

    private static class JaJp extends LanguageProvider {
        public JaJp(PackOutput output) {
            super(output, NayutaChest.MODID, "ja_jp");
        }

        @Override
        protected void addTranslations() {
            add(ModCreativeModeTabs.MOD_TAB_ID, "那由多チェスト");
            add(NayutaChestBE.TITLE_KEY, "那由多チェスト");
            addBlock(ModBlocks.NAYUTA_CHEST, "那由多チェスト");
        }
    }
}
