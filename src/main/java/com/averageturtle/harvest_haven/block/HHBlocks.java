package com.averageturtle.harvest_haven.block;

import com.averageturtle.harvest_haven.HarvestHaven;
import com.averageturtle.harvest_haven.block.entity.ChickenNestBlockEntity;
import com.averageturtle.harvest_haven.block.entity.CookingPotBlockEntity;
import com.averageturtle.harvest_haven.item.HHItems;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class HHBlocks {

	public static final ChickenNest CHICKEN_NEST = new ChickenNest(QuiltBlockSettings.of(Material.EGG, MapColor.OFF_WHITE).strength(0.0F).sounds(BlockSoundGroup.GRASS).ticksRandomly().nonOpaque().noCollision());
	public static final CookingPot COOKING_POT = new CookingPot(QuiltBlockSettings.of(Material.METAL, MapColor.GRAY).strength(4.0F).sounds(BlockSoundGroup.METAL).nonOpaque());

	public static final BlockEntityType<ChickenNestBlockEntity> CHICKEN_NEST_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(ChickenNestBlockEntity::new, CHICKEN_NEST).build();
	public static final BlockEntityType<CookingPotBlockEntity> COOKING_POT_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder.create(CookingPotBlockEntity::new, COOKING_POT).build();

	public static void RegisterBlocks() {
		HarvestHaven.LOGGER.info("Registering blocks!");

		RegisterBlock("chicken_nest", ItemGroups.NATURAL, CHICKEN_NEST);
		RegisterBlock("cooking_pot", ItemGroups.FUNCTIONAL, COOKING_POT);

		Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HarvestHaven.MODID, "chicken_nest"), CHICKEN_NEST_BLOCK_ENTITY);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HarvestHaven.MODID, "cooking_pot"), COOKING_POT_BLOCK_ENTITY);
	}
	static void RegisterBlock(final String name, ItemGroup itemGroup,final Block block) {
		Registry.register(Registries.BLOCK, new Identifier(HarvestHaven.MODID, name), block);
		HHItems.RegisterItem(name, new BlockItem(block, new QuiltItemSettings()), itemGroup);
	}

}
