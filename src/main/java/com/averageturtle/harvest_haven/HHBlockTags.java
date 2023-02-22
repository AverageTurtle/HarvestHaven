package com.averageturtle.harvest_haven;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class HHBlockTags {
	public static final TagKey<Block> VALID_NEST_PLACEMENT = TagKey.of(RegistryKeys.BLOCK, new Identifier(HarvestHaven.MODID, "valid_nest_placement"));
}
