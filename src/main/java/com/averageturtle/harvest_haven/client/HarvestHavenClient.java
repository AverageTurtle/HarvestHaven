package com.averageturtle.harvest_haven.client;

import com.averageturtle.harvest_haven.block.HHBlocks;
import com.averageturtle.harvest_haven.block.entity.CookingPotBlockEntity;
import com.averageturtle.harvest_haven.client.render.block.CookingPotBlockEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;


@ClientOnly
public class HarvestHavenClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		//HarvestHaven.LOGGER.info("Harvest Haven Client");
		BlockRenderLayerMap.put(RenderLayer.getCutout(), HHBlocks.CHICKEN_NEST);

		//Cooking pot rendering
		BlockEntityRendererFactories.register(HHBlocks.COOKING_POT_BLOCK_ENTITY, CookingPotBlockEntityRenderer::new);

		//Cooking pot update inv packet
		ClientPlayNetworking.registerGlobalReceiver(CookingPotBlockEntity.UPDATE_INV_PACKET_ID, ((client, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			int mixingTime = buf.readInt();
			DefaultedList<ItemStack> inv = DefaultedList.ofSize(CookingPotBlockEntity.INVENTORY_SIZE, ItemStack.EMPTY);
			for (int i = 0; i < CookingPotBlockEntity.INVENTORY_SIZE; i++) {
				inv.set(i, buf.readItemStack());
			}
			client.execute(() -> {
				assert MinecraftClient.getInstance().world != null;
				CookingPotBlockEntity blockEntity = (CookingPotBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);
				assert blockEntity != null;
				blockEntity.mixingTime = mixingTime;
				for (int i = 0; i < CookingPotBlockEntity.INVENTORY_SIZE; i++) {
					blockEntity.setStack(i, inv.get(i));
				}
			});
		}));
	}
}
