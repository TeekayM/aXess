package net.teekay.axess.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
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
                .pattern(" C ")
                .pattern(" BA")
                .pattern(" B ")
                .define('A', Items.REDSTONE)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.AMETHYST_SHARD)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.KEYCARD_READER.get())
                .pattern("QSQ")
                .pattern("STS")
                .pattern("QSQ")
                .define('S', Items.IRON_INGOT)
                .define('T', Blocks.REDSTONE_BLOCK)
                .define('Q', Items.QUARTZ)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.KEYCARD_EDITOR.get())
                .pattern("AQD")
                .pattern("SRS")
                .pattern("SBS")
                .define('S', Items.IRON_INGOT)
                .define('B', Blocks.IRON_BLOCK)
                .define('R', Blocks.REDSTONE_BLOCK)
                .define('A', Items.AMETHYST_SHARD)
                .define('Q', Items.QUARTZ)
                .define('D', Items.DIAMOND)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.NETWORK_MANAGER.get())
                .pattern(" R ")
                .pattern("SRS")
                .pattern("QBQ")
                .define('S', Items.IRON_INGOT)
                .define('B', Blocks.IRON_BLOCK)
                .define('R', Items.REDSTONE_BLOCK)
                .define('Q', Blocks.QUARTZ_BLOCK)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.RECEIVER.get())
                .pattern("   ")
                .pattern(" T ")
                .pattern("SQS")
                .define('S', Items.IRON_INGOT)
                .define('Q', Blocks.QUARTZ_BLOCK)
                .define('T', Items.REDSTONE_TORCH)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.MINI_KEYCARD_READER_LEFT.get())
                .pattern("   ")
                .pattern("ST ")
                .pattern("   ")
                .define('S', Items.IRON_INGOT)
                .define('T', AxessBlockRegistry.KEYCARD_READER.get())
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.MINI_KEYCARD_READER_RIGHT.get())
                .pattern("   ")
                .pattern(" TS")
                .pattern("   ")
                .define('S', Items.QUARTZ)
                .define('T', AxessBlockRegistry.KEYCARD_READER.get())
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessItemRegistry.KEYCARD.get())
                .pattern(" Q ")
                .pattern("ABA")
                .pattern(" Q ")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.REDSTONE_TORCH)
                .define('Q', Items.QUARTZ)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AxessBlockRegistry.LOCKDOWN_RECEIVER.get())
                .pattern(" Q ")
                .pattern("QBQ")
                .pattern(" Q ")
                .define('B', AxessBlockRegistry.RECEIVER.get())
                .define('Q', Items.IRON_BARS)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

    }
}
