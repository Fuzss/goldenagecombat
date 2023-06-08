package fuzs.goldenagecombat.data;

import fuzs.goldenagecombat.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends AbstractTagProvider.Items{

    public ModItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, lookupProvider, modId, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.EXCLUDED_FROM_SWORD_BLOCKING_ITEM_TAG);
        this.tag(ModRegistry.INCLUDED_FOR_SWORD_BLOCKING_ITEM_TAG);
        this.tag(ModRegistry.OVERRIDES_SWORD_BLOCKING_ITEM_TAG);
    }
}
