package com.averageturtle.harvest_haven.block.goals;

import com.averageturtle.harvest_haven.block.ChickenNest;
import com.averageturtle.harvest_haven.block.HHBlocks;
import com.averageturtle.harvest_haven.entity.ChickenEntityInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public class FindNest extends MoveToTargetPosGoal {
	private static final Block TARGET_BLOCK = HHBlocks.CHICKEN_NEST;

	public ChickenEntity chickenEntity;

	public FindNest(ChickenEntity chicken, double speed, int range, int maxYDifference) {
		super(chicken, speed, range, maxYDifference);
		this.chickenEntity = chicken;

	}


	@Override
	public boolean canStart() {
		//Boolean hasTarget = true;
		return ((ChickenEntityInterface)chickenEntity).gethhEggTimer() <= 0 && super.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return !((ChickenEntityInterface)chickenEntity).layEgg();
	}

	@Override
	public void stop() {

	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	protected boolean isTargetPos(WorldView world, BlockPos pos) {
		Chunk chunk = world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
		if (chunk == null) {
			return false;
		} else {
			BlockState state = chunk.getBlockState(pos);
			return state.isOf(TARGET_BLOCK) && state.get(ChickenNest.EGG_COUNT) != 4;
		}
	}
}
