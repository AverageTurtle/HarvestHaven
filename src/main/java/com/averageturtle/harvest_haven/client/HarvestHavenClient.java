package com.averageturtle.harvest_haven.client;

import com.averageturtle.harvest_haven.HarvestHaven;
import com.averageturtle.harvest_haven.block.HHBlocks;
import net.minecraft.client.render.RenderLayer;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

public class HarvestHavenClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		//HarvestHaven.LOGGER.info("Harvest Haven Client");
		BlockRenderLayerMap.put(RenderLayer.getCutout(), HHBlocks.CHICKEN_NEST);
	}
}
