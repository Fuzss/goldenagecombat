package fuzs.goldenagecombat.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator p_125973_) {
        super(p_125973_);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> p_176532_) {
        ShapedRecipeBuilder.shaped(Items.ENCHANTED_GOLDEN_APPLE)
                .define('#', Blocks.GOLD_BLOCK)
                .define('A', Items.APPLE)
                .pattern("###")
                .pattern("#A#")
                .pattern("###")
                .unlockedBy("has_gold_block", has(Blocks.GOLD_BLOCK))
                .save(p_176532_);
    }
}
