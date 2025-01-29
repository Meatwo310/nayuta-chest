package io.github.meatwo310.nayutachest.client.screen;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.config.ClientConfig;
import io.github.meatwo310.nayutachest.handler.NayutaChestHandler;
import io.github.meatwo310.nayutachest.menu.NayutaChestMenu;
import io.github.meatwo310.nayutachest.util.IntShift;
import io.github.meatwo310.nayutachest.util.NumberFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraftforge.client.extensions.IForgeGuiGraphics;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.math.BigInteger;

public class NayutaChestMenuScreen extends AbstractContainerScreen<NayutaChestMenu> {
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation TEXTURE = new ResourceLocation(NayutaChest.MODID, "textures/gui/nayutachest_screen.png");
    private static final int FONT_COLOR = new IForgeGuiGraphics(){}.getColorFromFormattingCharacter('f', false);
    private static final boolean FONT_SHADOW = false;

    public NayutaChestMenuScreen(NayutaChestMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY += 1;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float tick, int x, int y) {
        renderBackground(guiGraphics);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float tick) {
        super.render(guiGraphics, mouseX, mouseY, tick);

        ContainerData containerData = this.menu.containerData;

//        StringBuilder logMessage = new StringBuilder("ContainerData: ");
//        for (int i = 0; i < NayutaChestBE.NayutaChestContainerData.DATA_SIZE; i++) {
//            logMessage.append("[%d]:%d, ".formatted(i, containerData.get(i)));
//        }
//        LOGGER.info(logMessage.toString());

        int precision = ClientConfig.PRECISION.get();

        BigInteger inserted = new IntShift(
                containerData.get(NayutaChestBE.NayutaChestContainerData.INSERTED_AVG_BASE),
                containerData.get(NayutaChestBE.NayutaChestContainerData.INSERTED_AVG_SHIFT)
        ).toBigInteger();
        String insertedString = new NumberFormatter(inserted).to(ClientConfig.NUMBER_FORMAT.get(), precision) + " /t";

        BigInteger extracted = new IntShift(
                containerData.get(NayutaChestBE.NayutaChestContainerData.EXTRACTED_AVG_BASE),
                containerData.get(NayutaChestBE.NayutaChestContainerData.EXTRACTED_AVG_SHIFT)
        ).toBigInteger();
        String extractedString = new NumberFormatter(extracted).to(ClientConfig.NUMBER_FORMAT.get(), precision) + " /t";

        String itemCountString;
        LazyOptional<NayutaChestHandler> handlerLazyOptional = this.menu.nayutaChestBlock.chestHandlerLazyOptional;
        if (handlerLazyOptional.isPresent()) {
            NayutaChestHandler handler = handlerLazyOptional.orElseThrow(() -> new IllegalStateException("NayutaChestHandler is not present"));
            itemCountString = new NumberFormatter(handler.getStackCount()).to(ClientConfig.NUMBER_FORMAT.get(), precision);
        } else {
            itemCountString = "ERROR";
        }

        Font font = Minecraft.getInstance().font;
        int insertedX = 43 + this.leftPos - font.width(insertedString) / 2;
        int extractedX = 133 + this.leftPos - font.width(extractedString) / 2;
        int itemCountX = 88 + this.leftPos - font.width(itemCountString) / 2;
        int insExtY = 56 + this.topPos;
        int itemCountY = 21 + this.topPos;

        guiGraphics.drawString(font, insertedString, insertedX, insExtY, FONT_COLOR, FONT_SHADOW);
        guiGraphics.drawString(font, extractedString, extractedX, insExtY, FONT_COLOR, FONT_SHADOW);
        guiGraphics.drawString(font, itemCountString, itemCountX, itemCountY, FONT_COLOR, FONT_SHADOW);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
