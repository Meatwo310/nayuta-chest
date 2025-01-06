package io.github.meatwo310.nayutachest.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.handler.NayutaChestDisplayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Quaternionf;

public class NayutaChestBERenderer implements BlockEntityRenderer<NayutaChestBE> {
    public NayutaChestBERenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(NayutaChestBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        Direction direction = blockEntity.getDirection();
        poseStack.pushPose();
        poseStack.translate(0.5, 0.3, 0.5);
        poseStack.scale(1.2f, 1.2f, 1.2f);
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(switch (direction) {
            case SOUTH -> 0;
            case EAST -> 90;
            case NORTH -> 180;
            case WEST -> 270;
            default -> 0;
        })));

        blockEntity.chestHandlerLazyOptional.ifPresent(handler -> itemRenderer.renderStatic(
                handler.getStackInSlot(NayutaChestDisplayHandler.SLOT_OUTPUT),
                ItemDisplayContext.GROUND,
                getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                1
        ));

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos blockPos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, blockPos);
        int skyLight = level.getBrightness(LightLayer.SKY, blockPos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
