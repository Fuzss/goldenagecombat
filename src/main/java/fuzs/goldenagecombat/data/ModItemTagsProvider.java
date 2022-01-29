package fuzs.goldenagecombat.data;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.registry.ModRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.nio.file.Path;

public class ModItemTagsProvider extends TagsProvider<Item> {
    public ModItemTagsProvider(DataGenerator p_126546_, ExistingFileHelper fileHelperIn) {
        super(p_126546_, Registry.ITEM, GoldenAgeCombat.MOD_ID, fileHelperIn);
    }

    @Override
    protected void addTags() {
        this.tag(ModRegistry.SWORD_BLOCKING_EXCLUSIONS_TAG);
        this.tag(ModRegistry.SWORD_BLOCKING_INCLUSIONS_TAG);
    }

    @Override
    protected Path getPath(ResourceLocation p_126537_) {
        return this.generator.getOutputFolder().resolve("data/" + p_126537_.getNamespace() + "/tags/items/" + p_126537_.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Item Tags";
    }
}
