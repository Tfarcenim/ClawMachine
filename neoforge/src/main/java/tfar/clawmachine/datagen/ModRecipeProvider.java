package tfar.clawmachine.datagen;

import com.google.common.collect.Maps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import tfar.clawmachine.ModItems;

import java.util.EnumMap;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    static final EnumMap<DyeColor, Block> TERRACOTTA = Maps.newEnumMap(DyeColor.class);

    static {
        TERRACOTTA.put(DyeColor.RED, Blocks.RED_TERRACOTTA);
        TERRACOTTA.put(DyeColor.GREEN, Blocks.GREEN_TERRACOTTA);
        TERRACOTTA.put(DyeColor.ORANGE, Blocks.ORANGE_TERRACOTTA);
        TERRACOTTA.put(DyeColor.MAGENTA, Blocks.MAGENTA_TERRACOTTA);
        TERRACOTTA.put(DyeColor.CYAN, Blocks.CYAN_TERRACOTTA);
        TERRACOTTA.put(DyeColor.PINK, Blocks.PINK_TERRACOTTA);
        TERRACOTTA.put(DyeColor.GRAY, Blocks.GRAY_TERRACOTTA);
        TERRACOTTA.put(DyeColor.BROWN, Blocks.BROWN_TERRACOTTA);
        TERRACOTTA.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_TERRACOTTA);
        TERRACOTTA.put(DyeColor.LIME, Blocks.LIME_TERRACOTTA);
        TERRACOTTA.put(DyeColor.WHITE, Blocks.WHITE_TERRACOTTA);
        TERRACOTTA.put(DyeColor.YELLOW, Blocks.YELLOW_TERRACOTTA);
        TERRACOTTA.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_TERRACOTTA);
        TERRACOTTA.put(DyeColor.BLACK, Blocks.BLACK_TERRACOTTA);
        TERRACOTTA.put(DyeColor.BLUE, Blocks.BLUE_TERRACOTTA);
        TERRACOTTA.put(DyeColor.PURPLE, Blocks.PURPLE_TERRACOTTA);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ModItems.CLAW_MACHINES.map().forEach((dyeColor, blockItem) -> {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC,blockItem)
                    .define('a', TERRACOTTA.get(dyeColor))
                    .define('b', Blocks.GLASS_PANE)
                    .define('c', Blocks.CHAIN)
                    .define('d', Blocks.HOPPER)
                    .define('e', Blocks.LEVER)
                    .define('f', Blocks.STONE_BUTTON)
                    .pattern("aaa")
                    .pattern("bcb")
                    .pattern("def")
                    .unlockedBy("d", has(Blocks.HOPPER))
                    .save(recipeOutput);
        });

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.KEY)
                .define('i', Items.IRON_INGOT)
                .define('t', Blocks.TRIPWIRE_HOOK)
                .pattern(" i ")
                .pattern("i i")
                .pattern(" t ")
                .unlockedBy("i", has(Blocks.TRIPWIRE_HOOK))
                .save(recipeOutput);
    }
}
