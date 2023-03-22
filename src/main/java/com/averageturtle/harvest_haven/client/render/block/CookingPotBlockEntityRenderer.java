package com.averageturtle.harvest_haven.client.render.block;

import com.averageturtle.harvest_haven.block.entity.CookingPotBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class CookingPotBlockEntityRenderer implements BlockEntityRenderer<CookingPotBlockEntity> {

	public CookingPotBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}
	@Override
	public void render(CookingPotBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if(blockEntity.getWorld() == null)
			return;

		//RenderItems
		double posY = 0.1;
		for(int i = 0; i < CookingPotBlockEntity.INVENTORY_SIZE; i++) {
			ItemStack stack = blockEntity.getStack(i);
			if(stack == ItemStack.EMPTY)
				continue;

			matrices.push();

			matrices.translate(0.5, posY, 0.5);
			//matrices.scale(50.0f, 50.0f, 50.0f);

			int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos());
			MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers,lightAbove, overlay,MinecraftClient.getInstance().getItemRenderer().getModels().getModel(stack));
			matrices.pop();
			posY += 0.25;
		}
	}
}
