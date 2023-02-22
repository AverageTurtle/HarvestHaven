package com.averageturtle.harvest_haven.mixin;

import com.averageturtle.harvest_haven.HarvestHaven;
import com.averageturtle.harvest_haven.block.ChickenNest;
import com.averageturtle.harvest_haven.block.HHBlocks;
import com.averageturtle.harvest_haven.block.goals.FindNest;
import com.averageturtle.harvest_haven.entity.ChickenEntityInterface;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(ChickenEntity.class)
public class ChickenEntityMixin extends AnimalEntity implements ChickenEntityInterface {
	@Shadow
	private static Ingredient BREEDING_INGREDIENT;

	public boolean fertilized = false;
	private int hhEggLayTime = this.random.nextInt(6000) + 6000;
	protected ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "initGoals", at = @At("TAIL"))
	public void harvest_haven$initGoals(CallbackInfo ci) {
		this.goalSelector.add(2, new FindNest((ChickenEntity)(Object)this, 1.2, 24, 8));
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "tickMovement", at = @At("TAIL"))
	public void harvest_haven$tickMovement(CallbackInfo ci) {
		((ChickenEntity)(Object)this).eggLayTime = 6000;
		hhEggLayTime--;
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void harvest_haven$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		if(nbt.contains("harvest_haven:EggLayTime")) {
			this.hhEggLayTime = nbt.getInt("harvest_haven:EggLayTime");
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void harvest_haven$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putInt("harvest_haven:EggLayTime", this.hhEggLayTime);
	}

	@Override
	public void breed(ServerWorld world, AnimalEntity other) {
		ServerPlayerEntity serverPlayerEntity = this.getLovingPlayer();
		if (serverPlayerEntity == null && other.getLovingPlayer() != null) {
			serverPlayerEntity = other.getLovingPlayer();
		}

		if (serverPlayerEntity != null) {
			serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
			Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this, other, null);
		}

		this.setBreedingAge(6000);
		other.setBreedingAge(6000);
		this.resetLoveTicks();
		other.resetLoveTicks();
		world.sendEntityStatus(this, (byte)18);
		if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
			world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
		}
		fertilized = true;
		hhEggLayTime = 0;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return EntityType.CHICKEN.create(world);
	}

	@Override
	public boolean fertilized() {
		return fertilized;
	}

	@Override
	public int gethhEggTimer() {
		return hhEggLayTime;
	}

	@Override
	public boolean layEgg() {
		BlockState state = getBlockStateAtPos();
		if(state.isOf(HHBlocks.CHICKEN_NEST) && state.get(ChickenNest.EGG_COUNT) != 4) {
			this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			Integer egg_count = state.get(ChickenNest.EGG_COUNT);
			Integer fertile = state.get(ChickenNest.FERTILE);

			if(fertilized) {
				world.setBlockState(getBlockPos(), state.with(ChickenNest.EGG_COUNT,egg_count+1).with(ChickenNest.FERTILE, fertile+1));
			}else {
				world.setBlockState(getBlockPos(), state.with(ChickenNest.EGG_COUNT,egg_count+1));
			}

			hhEggLayTime = random.nextInt(6000) + 6000;
			fertilized = false;
			return true;
		}
		return false;

	}
}
