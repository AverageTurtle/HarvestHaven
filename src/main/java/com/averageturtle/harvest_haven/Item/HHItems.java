package com.averageturtle.harvest_haven.Item;

import com.averageturtle.harvest_haven.HarvestHaven;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class HHItems {
	public static void RegisterItems() {
		HarvestHaven.LOGGER.info("Registering items!");
		RegisterItem("test_item" ,new Item(new QuiltItemSettings()));
		RegisterItem("second_test" ,new Item(new QuiltItemSettings()));
	}

	static void RegisterItem(final String name, final Item item) {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.addItem(item));
		Registry.register(Registries.ITEM, new Identifier(HarvestHaven.MODID, name), item);
	}
}
