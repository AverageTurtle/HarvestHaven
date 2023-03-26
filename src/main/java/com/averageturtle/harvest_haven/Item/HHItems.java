package com.averageturtle.harvest_haven.item;

import com.averageturtle.harvest_haven.HarvestHaven;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class HHItems {

	public static Item WOODEN_MIXING_SPOON = new Item(new QuiltItemSettings());
	public static void RegisterItems() {
		HarvestHaven.LOGGER.info("Registering items!");
		RegisterItem("wooden_mixing_spoon", WOODEN_MIXING_SPOON, ItemGroups.TOOLS_AND_UTILITIES);
		//RegisterItem("second_test" ,new Item(new QuiltItemSettings()), ItemGroups.OP);~
	}

	public static void RegisterItem(final String name, final Item item, final ItemGroup[] itemGroups) {
		for (ItemGroup itemGroup : itemGroups) {
			ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addItem(item));
		}
		Registry.register(Registries.ITEM, new Identifier(HarvestHaven.MODID, name), item);
	}
	public static void RegisterItem(final String name, final Item item, final ItemGroup itemGroup) {
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.addItem(item));
		Registry.register(Registries.ITEM, new Identifier(HarvestHaven.MODID, name), item);
	}
	public static void RegisterItem(final String name, final Item item) {
		Registry.register(Registries.ITEM, new Identifier(HarvestHaven.MODID, name), item);
	}
}
