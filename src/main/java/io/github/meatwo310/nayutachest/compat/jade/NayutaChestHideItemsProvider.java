package io.github.meatwo310.nayutachest.compat.jade;

import io.github.meatwo310.nayutachest.NayutaChest;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import javax.annotation.Nullable;
import java.util.List;

public class NayutaChestHideItemsProvider implements IServerExtensionProvider<NayutaChestBE, ItemStack> {
    public static final ResourceLocation UID = NayutaChest.getResourceLoc("hide_items");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    @Nullable
    public List<ViewGroup<ItemStack>> getGroups(ServerPlayer player, ServerLevel world, NayutaChestBE target, boolean showDetails) {
        return List.of();
    }
}
