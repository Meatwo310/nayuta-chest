package io.github.meatwo310.nayutachest.client.screen;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.menu.NayutaChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class NayutaChestMenuScreen extends AbstractContainerScreen<NayutaChestMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(NayutaChest.MODID, "textures/gui/nayutachest_screen.png");

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
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, float tick) {
        super.render(guiGraphics, x, y, tick);
        renderTooltip(guiGraphics, x, y);
    }
}
