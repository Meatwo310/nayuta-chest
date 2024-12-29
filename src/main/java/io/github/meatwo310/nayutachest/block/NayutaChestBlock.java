package io.github.meatwo310.nayutachest.block;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.blockentity.ModBlockEntities;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.handler.NayutaChestHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NayutaChestBlock extends Block implements EntityBlock {
    private final NayutaChestHandler inventory = new NayutaChestHandler();

    public NayutaChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return ModBlockEntities.NAYUTA_CHEST.get().create(blockPos, blockState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof NayutaChestBE) {
                ItemStack itemStack = new ItemStack(this);
                blockEntity.saveToItem(itemStack);
                String emptyNbt = "{BlockEntityTag:{" + NayutaChest.MODID + ":{},id:\"" + ModBlocks.NAYUTA_CHEST.getId().toString() + "\"}}";
                if (itemStack.getTag() != null && itemStack.getTag().toString().equals(emptyNbt)) {
                    itemStack.setTag(null);
                }
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }


}
