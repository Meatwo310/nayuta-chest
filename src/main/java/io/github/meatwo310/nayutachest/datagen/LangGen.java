package io.github.meatwo310.nayutachest.datagen;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.block.ModBlocks;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.client.screen.NayutaChestMenuScreen;
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

            add(NayutaChestMenuScreen.ITEM_KEY, "Item: %s");
            add(NayutaChestMenuScreen.USAGE_KEY, "Usage: %s");
            add(NayutaChestMenuScreen.AMOUNT_KEY, "Amount: %s");
            add(NayutaChestMenuScreen.CAPACITY_KEY, "Capacity: %s");
            add(NayutaChestMenuScreen.INSERTION_RATE_KEY, "Insertion Rate: %s");
            add(NayutaChestMenuScreen.EXTRACTION_RATE_KEY, "Extraction Rate: %s");
            add(NayutaChestMenuScreen.BALANCE_KEY, "Balance: %s");

            add("config.jade.plugin_nayutachest.contents", "Show accurate item count");
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

            add(NayutaChestMenuScreen.ITEM_KEY, "アイテム: ");
            add(NayutaChestMenuScreen.USAGE_KEY, "使用率: ");
            add(NayutaChestMenuScreen.AMOUNT_KEY, "保管済み: ");
            add(NayutaChestMenuScreen.CAPACITY_KEY, "最大容量: ");
            add(NayutaChestMenuScreen.INSERTION_RATE_KEY, "搬入レート: ");
            add(NayutaChestMenuScreen.EXTRACTION_RATE_KEY, "搬出レート: ");
            add(NayutaChestMenuScreen.BALANCE_KEY, "収支: ");

            add("config.jade.plugin_nayutachest.contents", "正確なアイテム数を表示");
        }
    }
}
