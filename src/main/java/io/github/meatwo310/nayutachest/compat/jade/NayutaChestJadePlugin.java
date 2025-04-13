package io.github.meatwo310.nayutachest.compat.jade;

import io.github.meatwo310.nayutachest.block.NayutaChestBlock;
import io.github.meatwo310.nayutachest.blockentity.NayutaChestBE;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class NayutaChestJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerItemStorage(new NayutaChestHideItemsProvider(), NayutaChestBE.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(NayutaChestComponentProvider.INSTANCE, NayutaChestBlock.class);
    }
}
