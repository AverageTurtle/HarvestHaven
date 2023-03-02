package com.averageturtle.harvest_haven.client.render.block;

import com.averageturtle.harvest_haven.block.entity.CookingPotBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class CookingPotBlockEntityRenderer implements BlockEntityRenderer<CookingPotBlockEntity> {
	// A jukebox itemstack
	private static ItemStack stack = new ItemStack(Items.DIAMOND, 1);

	public CookingPotBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}
	@Override
	public void render(CookingPotBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if(blockEntity.getWorld() == null)
			return;

		matrices.push();
		// Move the item
		matrices.translate(0.5, 0.1, 0.5);
		//matrices.scale(50.0f, 50.0f, 50.0f);

		int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos());
		MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, 0);
		matrices.pop();
	}
}
