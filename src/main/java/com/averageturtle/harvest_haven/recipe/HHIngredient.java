package com.averageturtle.harvest_haven.recipe;

import com.averageturtle.harvest_haven.HarvestHaven;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public class HHIngredient {
	public record HHGenericIngredient(IngredientType type, Object ingredient) { }
	public record HHItemIngredient(Ingredient ingredient, int count) {
		public static final HHItemIngredient EMPTY = new HHItemIngredient(Ingredient.EMPTY, 0);

		public static HHItemIngredient fromPacket(PacketByteBuf buf) {
			return new HHItemIngredient(Ingredient.fromPacket(buf), buf.readInt());
		}

		public void write(PacketByteBuf buf) {
			ingredient.write(buf);
			buf.writeInt(count);
		}
	}

	public enum IngredientType {
		Invalid, Item, Fluid;
	}


	public static HHGenericIngredient FromJson(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull())
			throw new JsonSyntaxException("Ingredient cannot be null");
		if(!json.isJsonObject())
			throw new JsonSyntaxException("Ingredient must be a object");

		JsonObject jsonObject = json.getAsJsonObject();
		if (jsonObject.has("item") && jsonObject.has("tag"))
			throw new JsonParseException("An ingredient entry is either a tag or an item, not both");

		int count = 1;
		if(jsonObject.has("count"))
			count = JsonHelper.getInt(jsonObject, "count");

		if (jsonObject.has("item")) {
			Item item = ShapedRecipe.getItem(jsonObject);
			return new HHGenericIngredient(IngredientType.Item, new HHItemIngredient(Ingredient.ofStacks(new ItemStack(item)), count));
		} else if (jsonObject.has("tag")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "tag"));
			TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, identifier);
			return new HHGenericIngredient(IngredientType.Item, new HHItemIngredient(Ingredient.ofTag(tagKey), count));
		}

		throw new JsonSyntaxException("Could not read HHIngredient from json");
	}
}
