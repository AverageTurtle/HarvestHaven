package com.averageturtle.harvest_haven;

import com.averageturtle.harvest_haven.block.HHBlocks;
import com.averageturtle.harvest_haven.item.HHItems;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HarvestHaven implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Harvest Haven");
	public static final String MODID = "harvest_haven";


	@Override
	public void onInitialize(ModContainer mod) {
		HHItems.RegisterItems();
		HHBlocks.RegisterBlocks();
	}
}
