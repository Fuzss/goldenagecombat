package fuzs.goldenagecombat.data.tags;

import fuzs.goldenagecombat.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeTagsProvider extends AbstractTagProvider<DamageType> {

    public ModDamageTypeTagsProvider(DataProviderContext context) {
        super(Registries.DAMAGE_TYPE, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.BYPASSES_SWORD_BLOCK_DAMAGE_TYPE_TAG).addTag(DamageTypeTags.BYPASSES_SHIELD);
    }
}
