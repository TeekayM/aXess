package net.teekay.axess.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.teekay.axess.registry.AxessBlockRegistry;
import net.teekay.axess.registry.AxessItemRegistry;

import java.util.function.Consumer;

public class AxessRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public AxessRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessItemRegistry.ACCESS_WRENCH.get())
                .pattern(" A ")
                .pattern(" B ")
                .pattern(" B ")
                .define('A', Items.REDSTONE)
                .define('B', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessItemRegistry.READER_LINKER.get())
                .pattern(" A ")
                .pattern(" BB")
                .pattern(" B ")
                .define('A', Items.REDSTONE)
                .define('B', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.KEYCARD_READER.get())
                .pattern("SSS")
                .pattern("S S")
                .pattern("SSS")
                .define('S', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.KEYCARD_EDITOR.get())
                .pattern("SSS")
                .pattern("SRS")
                .pattern("BBB")
                .define('S', Items.IRON_INGOT)
                .define('B', Blocks.IRON_BLOCK)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.NETWORK_MANAGER.get())
                .pattern(" R ")
                .pattern("SRS")
                .pattern("BBB")
                .define('S', Items.IRON_INGOT)
                .define('B', Blocks.IRON_BLOCK)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.RECEIVER.get())
                .pattern("   ")
                .pattern(" T ")
                .pattern("SBS")
                .define('S', Items.IRON_INGOT)
                .define('B', Blocks.IRON_BLOCK)
                .define('T', Items.REDSTONE_TORCH)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

    }
}
