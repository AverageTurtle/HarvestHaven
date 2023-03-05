package com.averageturtle.harvest_haven;

import com.averageturtle.harvest_haven.block.CookingPot;
import com.averageturtle.harvest_haven.block.HHBlocks;
import com.averageturtle.harvest_haven.item.HHItems;
import com.averageturtle.harvest_haven.recipe.CookingPotRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HarvestHaven implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Harvest Haven");
	public static final String MODID = "harvest_haven";

	public static final RecipeType<CookingPotRecipe> COOKING_POT_RECIPE_TYPE = new RecipeType<>() {};
	public static final RecipeSerializer<CookingPotRecipe> COOKING_POT_RECIPE_SERIALIZER = new CookingPotRecipe.Serializer();

	@Override
	public void onInitialize(ModContainer mod) {
		HHItems.RegisterItems();
		HHBlocks.RegisterBlocks();

		Registry.register(Registries.RECIPE_TYPE, new Identifier(MODID, "cooking_pot"), COOKING_POT_RECIPE_TYPE);
		Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(MODID, "cooking_pot"), COOKING_POT_RECIPE_SERIALIZER);

	}
}
