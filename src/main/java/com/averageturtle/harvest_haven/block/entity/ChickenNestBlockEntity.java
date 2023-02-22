package com.averageturtle.harvest_haven.block.entity;

import com.averageturtle.harvest_haven.block.ChickenNest;
import com.averageturtle.harvest_haven.block.HHBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntity;

public class ChickenNestBlockEntity extends BlockEntity implements Inventory, QuiltBlockEntity {

	public ChickenNestBlockEntity(BlockPos pos, BlockState state) {
		super(HHBlocks.CHICK_NEST_BLOCK_ENTITY, pos, state);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public int getMaxCountPerStack() {
		return 4;
	}

	@Override
	public boolean isEmpty() {
		assert world != null;
		return world.getBlockState(pos).get(ChickenNest.EGG_COUNT) != 0;
	}

	@Override
	public ItemStack getStack(int slot) {
		assert slot == 0;
		assert world != null;
		Integer egg_count = world.getBlockState(pos).get(ChickenNest.EGG_COUNT);
		if(egg_count == 0) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(Items.EGG, egg_count);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		assert slot == 0;
		assert world != null;
		Integer egg_count = world.getBlockState(pos).get(ChickenNest.EGG_COUNT);
		if(egg_count == 0) {
			return ItemStack.EMPTY;
		}
		if(egg_count - amount <= 0) {
			ChickenNest.SetEggCount(0, world, pos);
			return new ItemStack(Items.EGG, egg_count);
		}

		egg_count -= amount;
		ChickenNest.SetEggCount(egg_count, world, pos);
		return new ItemStack(Items.EGG, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		assert world != null;
		Integer egg_count = world.getBlockState(pos).get(ChickenNest.EGG_COUNT);
		ChickenNest.SetEggCount(0, world, pos);
		return new ItemStack(Items.EGG, egg_count);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if(stack.isOf(Items.EGG)) {
			assert world != null;
			Integer egg_count = world.getBlockState(pos).get(ChickenNest.EGG_COUNT);
			egg_count += stack.getCount();
			if(egg_count > 4) {
				ChickenNest.SetEggCount(4, world, pos);
			}
			ChickenNest.SetEggCount(egg_count, world, pos);
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		if (this.world == null) {
			return false;
		} else if (this.world.getBlockEntity(this.pos) != this) {
			return false;
		} else {
			return !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
		}

	}

	@Override
	public void clear() {
		assert world != null;
		ChickenNest.SetEggCount(0, world, pos);
	}
}
