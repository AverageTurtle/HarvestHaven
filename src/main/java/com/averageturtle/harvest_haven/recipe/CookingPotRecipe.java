package com.averageturtle.harvest_haven.recipe;

import com.averageturtle.harvest_haven.HarvestHaven;
import com.averageturtle.harvest_haven.block.CookingPot;
import com.averageturtle.harvest_haven.block.entity.CookingPotBlockEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import com.averageturtle.harvest_haven.recipe.HHIngredient.HHGenericIngredient;
import com.averageturtle.harvest_haven.recipe.HHIngredient.HHItemIngredient;
import org.quiltmc.qsl.recipe.api.serializer.QuiltRecipeSerializer;


public class CookingPotRecipe implements Recipe<Inventory> {
	private final Identifier id;
	public final ItemStack result;
	public final DefaultedList<HHItemIngredient> input;

	public CookingPotRecipe(Identifier identifier, DefaultedList<HHItemIngredient> defaultedList,  ItemStack output) {
		this.id = identifier;
		this.result = output;
		this.input = defaultedList;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		if(inventory.isEmpty()) return false;

		DefaultedList<HHItemIngredient> notFound = DefaultedList.of();
		for(HHItemIngredient ingredient : input) {
			notFound.add(new HHItemIngredient(ingredient.ingredient(), ingredient.count()));
		}

		//HashMap<HHItemIngredient, Integer> ingredientHashMap = new HashMap<HHItemIngredient, Integer>(); //Ingredient to slot in target inventory
		int lastOpenSlot = -1;


		for(int i= 0; i < inventory.size(); i++) {
			ItemStack itemStack = inventory.getStack(i);
			if(itemStack == ItemStack.EMPTY) {
				lastOpenSlot = i;
				continue;
			}
			boolean invalid = true;
			for(int j = 0; j < notFound.size(); j++) {
				HHItemIngredient ingredient = notFound.get(j);
				if(ingredient.ingredient().test(itemStack) && ingredient.count() <= itemStack.getCount()) {
					//ingredientHashMap.put(ingredient, i);
					notFound.remove(j);
					invalid= false;
					break;
				}
			}
			if(invalid && !itemStack.isOf(result.getItem()))
				return false;
		}

		return notFound.size() == 0 && lastOpenSlot > -1;
	}

	@Override
	public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
		return this.getResult(registryManager).copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResult(DynamicRegistryManager registryManager) {
		return result;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HarvestHaven.COOKING_POT_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return HarvestHaven.COOKING_POT_RECIPE_TYPE;
	}

	public static class Serializer implements QuiltRecipeSerializer<CookingPotRecipe> {

		@Override
		public CookingPotRecipe read(Identifier id, JsonObject jsonObject) {
			DefaultedList<HHItemIngredient> ingredients = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));

			if (ingredients.isEmpty())
				throw new JsonParseException("No ingredients for cooking pot recipe");
			else if (ingredients.size() > CookingPotBlockEntity.INVENTORY_SIZE)
				throw new JsonParseException("Too many ingredients for cooking pot recipe");

			ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
			return new CookingPotRecipe(id, ingredients, itemStack);
		}

		private static DefaultedList<HHItemIngredient> getIngredients(JsonArray jsonArray) {
			DefaultedList<HHItemIngredient> ingredientsList = DefaultedList.of();

			for(int i = 0; i < jsonArray.size(); ++i) {
				HHGenericIngredient ingredient = HHIngredient.FromJson(jsonArray.get(i));
				if (ingredient.type() == HHIngredient.IngredientType.Item) {
					ingredientsList.add((HHItemIngredient)ingredient.ingredient());
				}
			}

			return ingredientsList;
		}

		@Override
		public CookingPotRecipe read(Identifier id, PacketByteBuf buf) {
			int i = buf.readVarInt();
			DefaultedList<HHItemIngredient> ingredients = DefaultedList.ofSize(i, HHItemIngredient.EMPTY);

			ingredients.replaceAll(ignored -> HHItemIngredient.fromPacket(buf));
			ItemStack itemStack = buf.readItemStack();
			return new CookingPotRecipe(id, ingredients, itemStack);
		}

		@Override
		public void write(PacketByteBuf buf, CookingPotRecipe recipe) {
			buf.writeVarInt(recipe.input.size());

			for(HHItemIngredient ingredient : recipe.input) {
				ingredient.write(buf);
			}
			buf.writeItemStack(recipe.result);
		}

		@Override
		public JsonObject toJson(CookingPotRecipe recipe) {
			//TODO
			HarvestHaven.LOGGER.warn("CookingPotRecipe.Serializer:toJson is not implemented!");
			return null;
		}
	}
}
