package io.github.meatwo310.nayutachest.client.screen;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.config.ClientConfig;
import io.github.meatwo310.nayutachest.config.ServerConfig;
import io.github.meatwo310.nayutachest.handler.NayutaChestHandler;
import io.github.meatwo310.nayutachest.menu.NayutaChestMenu;
import io.github.meatwo310.nayutachest.util.BigDecimalUtil;
import io.github.meatwo310.nayutachest.util.BigIntegerUtil;
import io.github.meatwo310.nayutachest.util.IntShift;
import io.github.meatwo310.nayutachest.util.NumberFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.IForgeGuiGraphics;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class NayutaChestMenuScreen extends AbstractContainerScreen<NayutaChestMenu> {
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation TEXTURE = new ResourceLocation(NayutaChest.MODID, "textures/gui/nayutachest_screen.png");
    private static final boolean FONT_SHADOW = false;
    private static final Component PER_TICK = Component.literal("/t").withStyle(ChatFormatting.GRAY);

    public static final int FONT_COLOR = new IForgeGuiGraphics(){}.getColorFromFormattingCharacter('f', false);
    public static final int FAKE_STACK_X = 80;
    public static final int FAKE_STACK_Y = 35;

    public static final String ITEM_KEY = "gui." + NayutaChest.MODID + ".item";
    public static final String USAGE_KEY = "gui." + NayutaChest.MODID + ".usage";
    public static final String AMOUNT_KEY = "gui." + NayutaChest.MODID + ".amount";
    public static final String CAPACITY_KEY = "gui." + NayutaChest.MODID + ".max";
    public static final String INSERTION_RATE_KEY = "gui." + NayutaChest.MODID + ".insertion_rate";
    public static final String EXTRACTION_RATE_KEY = "gui." + NayutaChest.MODID + ".extraction_rate";
    public static final String BALANCE_KEY = "gui." + NayutaChest.MODID + ".balance";

    int precision = 0;
    NumberFormatter.Format numFormat;
    BigInteger inserted = BigInteger.ZERO;
    String insertedString = "";
    BigInteger extracted = BigInteger.ZERO;
    String extractedString = "";
    NayutaChestHandler handler = null;
    BigInteger amount = BigInteger.ZERO;
    String itemCountString = "";

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
        ContainerData containerData = this.menu.containerData;
        this.precision = ClientConfig.PRECISION.get();
        this.numFormat = ClientConfig.NUMBER_FORMAT.get();

        this.inserted = new IntShift(
                containerData.get(NayutaChestBE.NayutaChestContainerData.INSERTED_AVG_BASE),
                containerData.get(NayutaChestBE.NayutaChestContainerData.INSERTED_AVG_SHIFT)
        ).toBigInteger();
        this.insertedString = new NumberFormatter(this.inserted).to(this.numFormat, this.precision);

        this.extracted = new IntShift(
                containerData.get(NayutaChestBE.NayutaChestContainerData.EXTRACTED_AVG_BASE),
                containerData.get(NayutaChestBE.NayutaChestContainerData.EXTRACTED_AVG_SHIFT)
        ).toBigInteger();
        this.extractedString = new NumberFormatter(this.extracted).to(this.numFormat, this.precision);

        LazyOptional<NayutaChestHandler> handlerLazyOptional = this.menu.nayutaChestBlock.chestHandlerLazyOptional;
        if (handlerLazyOptional.isPresent()) {
            this.handler = handlerLazyOptional.orElseThrow(IllegalStateException::new);
            this.amount = this.handler.getStackCount();
            this.itemCountString = new NumberFormatter(this.amount).to(this.numFormat, this.precision);
        } else {
            this.handler = null;
            this.amount = BigInteger.ZERO;
            this.itemCountString = "ERROR";
        }

        super.render(guiGraphics, mouseX, mouseY, tick);

        String insertedString = this.insertedString + "/t";
        String extractedString = this.extractedString + "/t";

        Font font = Minecraft.getInstance().font;
        int insertedX = 43 + this.leftPos - font.width(insertedString) / 2;
        int extractedX = 133 + this.leftPos - font.width(extractedString) / 2;
        int itemCountX = 88 + this.leftPos - font.width(this.itemCountString) / 2;
        int insExtY = 56 + this.topPos;
        int itemCountY = 21 + this.topPos;

        guiGraphics.drawString(font, insertedString, insertedX, insExtY, FONT_COLOR, FONT_SHADOW);
        guiGraphics.drawString(font, extractedString, extractedX, insExtY, FONT_COLOR, FONT_SHADOW);
        guiGraphics.drawString(font, this.itemCountString, itemCountX, itemCountY, FONT_COLOR, FONT_SHADOW);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        if (this.handler == null) return;

        ItemStack stack = this.handler.getStackInSlot(NayutaChestHandler.SLOT_OUTPUT);
        if (stack.isEmpty()) return;
        ItemStack fakeStack = stack.copyWithCount(1);
        guiGraphics.renderFakeItem(fakeStack, FAKE_STACK_X, FAKE_STACK_Y);

        if (!this.isHovering(FAKE_STACK_X - 2, FAKE_STACK_Y - 2, 20, 20, mouseX, mouseY)) return;

        BigInteger max = ServerConfig.storageSizeCache;
        BigDecimal usage = BigDecimalUtil.getRate(this.amount.multiply(BigIntegerUtil.HUNDRED), max, 2);
        BigInteger balance = this.inserted.subtract(this.extracted);

        List<Component> components = List.of(
                Component.empty().append(Component.translatable(ITEM_KEY).withStyle(ChatFormatting.YELLOW))
                        .append(stack.getHoverName()),
                Component.empty().append(Component.translatable(USAGE_KEY).withStyle(ChatFormatting.YELLOW))
                        .append(usage + "%"),
                Component.empty().append(Component.translatable(AMOUNT_KEY).withStyle(ChatFormatting.YELLOW))
                        .append(this.itemCountString),
                Component.empty().append(Component.translatable(CAPACITY_KEY).withStyle(ChatFormatting.YELLOW))
                        .append(new NumberFormatter(max).to(this.numFormat, this.precision)),
                Component.empty().append(Component.translatable(INSERTION_RATE_KEY).withStyle(ChatFormatting.YELLOW))
                        .append(this.insertedString)
                        .append(PER_TICK),
                Component.empty().append(Component.translatable(EXTRACTION_RATE_KEY).withStyle(ChatFormatting.YELLOW))
                        .append(this.extractedString)
                        .append(PER_TICK),
                Component.empty().append(Component.translatable(BALANCE_KEY).withStyle(ChatFormatting.YELLOW))
                        .append((balance.signum() > 0 ? "+" : "") + new NumberFormatter(balance).to(this.numFormat, this.precision))
                        .append(PER_TICK)
        );
        guiGraphics.renderComponentTooltip(this.font, components, mouseX - this.getGuiLeft(), mouseY - this.getGuiTop());
    }
}
