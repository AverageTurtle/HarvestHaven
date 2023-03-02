package com.averageturtle.harvest_haven.client;

import com.averageturtle.harvest_haven.block.HHBlocks;
import com.averageturtle.harvest_haven.client.render.block.CookingPotBlockEntityRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;


@ClientOnly
public class HarvestHavenClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		//HarvestHaven.LOGGER.info("Harvest Haven Client");
		BlockRenderLayerMap.put(RenderLayer.getCutout(), HHBlocks.CHICKEN_NEST);

		BlockEntityRendererFactories.register(HHBlocks.COOKING_POT_BLOCK_ENTITY, CookingPotBlockEntityRenderer::new);
	}
}
