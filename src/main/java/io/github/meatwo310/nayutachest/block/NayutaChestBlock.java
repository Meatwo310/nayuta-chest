package io.github.meatwo310.nayutachest.block;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.blockentity.ModBlockEntities;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import io.github.meatwo310.nayutachest.handler.NayutaChestHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

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

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, (MenuProvider) level.getBlockEntity(blockPos), blockPos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @ParametersAreNonnullByDefault
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        if (type != ModBlockEntities.NAYUTA_CHEST.get()) return null;
        return (level1, blockPos, blockState, t) ->
                NayutaChestBE.tick((ServerLevel) level1, blockPos, blockState, (NayutaChestBE) t);
    }
}
