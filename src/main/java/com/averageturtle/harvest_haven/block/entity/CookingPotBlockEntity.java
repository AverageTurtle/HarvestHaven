package com.averageturtle.harvest_haven.block.entity;

import com.averageturtle.harvest_haven.HarvestHaven;
import com.averageturtle.harvest_haven.block.HHBlocks;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntity;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.Collection;

public class CookingPotBlockEntity extends BlockEntity implements Inventory, QuiltBlockEntity {
	public static final int INVENTORY_SIZE = 5;
	public static final int RESERVE_SLOT = INVENTORY_SIZE-1;
	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

	public static final Identifier UPDATE_INV_PACKET_ID = new Identifier(HarvestHaven.MODID, "update_cooking_pot");

	public CookingPotBlockEntity(BlockPos pos, BlockState state) {
		super(HHBlocks.COOKING_POT_BLOCK_ENTITY, pos, state);
	}

	//TODO(Sam): Figure out a more efficient way to do this
	private Collection<ServerPlayerEntity> lastViewers = null;
	public void tick() {
		if (world != null && !world.isClient) {
			Collection<ServerPlayerEntity> viewers = PlayerLookup.tracking(this);
			if(lastViewers == null || viewers != lastViewers) {
				SendUpdatePacket();
			}
			lastViewers = viewers;
		}
	}


	public void SendUpdatePacket() {
		if (world != null && !world.isClient) {
			Collection<ServerPlayerEntity> viewers = PlayerLookup.tracking(this);
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeBlockPos(pos);
			for(int i = 0; i < CookingPotBlockEntity.INVENTORY_SIZE; i++) {
				buf.writeItemStack(getStack(i));
			}
			viewers.forEach(player -> ServerPlayNetworking.send(player, UPDATE_INV_PACKET_ID, buf));
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.readNbt(nbt, inventory);
		SendUpdatePacket();
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, inventory);
	}


	@Override
	public int size() { return inventory.size(); }

	@Override
	public boolean isEmpty() {
		for(ItemStack itemStack : inventory) {
			if (!itemStack.isEmpty()) { return false; }
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return slot >= 0 && slot < inventory.size() ? inventory.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack stack = Inventories.splitStack(inventory, slot, amount);
		//Note we may be sending unnecessary packets here
		SendUpdatePacket();
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack stack = Inventories.removeStack(inventory, slot);
		SendUpdatePacket();
		return stack;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (slot >= 0 && slot < this.inventory.size()) {
			inventory.set(slot, stack);
			SendUpdatePacket();
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
		inventory.clear();
		SendUpdatePacket();
	}
}
