package io.github.meatwo310.nayutachest.compat.jade;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.config.ClientConfig;
import io.github.meatwo310.nayutachest.config.ServerConfig;
import io.github.meatwo310.nayutachest.handler.NayutaChestHandler;
import io.github.meatwo310.nayutachest.util.BigDecimalUtil;
import io.github.meatwo310.nayutachest.util.NumberFormatter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressElement;
import snownee.jade.impl.ui.ProgressStyle;

import java.math.BigInteger;

public enum NayutaChestComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    public static final ResourceLocation UID = NayutaChest.getResourceLoc("contents");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        IElementHelper elementHelper = iTooltip.getElementHelper();

        var progressStyle = new ProgressStyle();
        progressStyle.color(0x7DE0D4);
        progressStyle.textColor(0xFFFFFF);

        if (!(blockAccessor.getBlockEntity() instanceof NayutaChestBE nayutaChestBE)) return;
        if (!nayutaChestBE.chestHandlerLazyOptional.isPresent()) return;

        NayutaChestHandler handler = nayutaChestBE.chestHandlerLazyOptional.orElseThrow(IllegalStateException::new);
        ItemStack stack = handler.getStackInSlot(NayutaChestHandler.SLOT_OUTPUT);

        if (stack.isEmpty()) return;

        BigInteger amount = handler.getStackCount();
        BigInteger max = ServerConfig.storageSizeCache;
        float usage = BigDecimalUtil.getRate(amount, max, 2).floatValue();

        String amountString = new NumberFormatter(amount).to(ClientConfig.NUMBER_FORMAT.get(), ClientConfig.PRECISION.get());

        iTooltip.add(elementHelper
                .item(stack.copyWithCount(1), 0.5f)
                .size(new Vec2(10.0f, 10.0f))
                .translate(new Vec2(0.0f, 1.0f))
        );
        iTooltip.append(new ProgressElement(
                usage,
                Component.literal(amountString + "× ").append(stack.getHoverName()),
                progressStyle,
                BoxStyle.DEFAULT,
                false
        ));
    }


    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
