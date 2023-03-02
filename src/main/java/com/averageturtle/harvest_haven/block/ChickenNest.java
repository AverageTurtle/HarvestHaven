package com.averageturtle.harvest_haven.block;

import com.averageturtle.harvest_haven.HHBlockTags;
import com.averageturtle.harvest_haven.block.entity.ChickenNestBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ChickenNest extends BlockWithEntity {
	public static final IntProperty EGG_COUNT = IntProperty.of("egg_count", 0, 4);
	public static final IntProperty FERTILE = IntProperty.of("fertile", 0, 4);

	public static boolean SetEggCount(Integer count, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		Integer egg_count = state.get(EGG_COUNT);
		if(egg_count <= 0 || count < 0 || count > 4) {
			return false;
		}

		egg_count = count;
		if(egg_count < state.get(FERTILE)) {
			world.setBlockState(pos, state.with(EGG_COUNT, egg_count).with(FERTILE, egg_count));
		} else {
			world.setBlockState(pos, state.with(EGG_COUNT, egg_count));
		}

		return true;
	}
	public ChickenNest(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(EGG_COUNT, 0).with(FERTILE, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(EGG_COUNT, FERTILE);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	//TODO (Sam) Figure out why these are deprecated
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		Integer fertile = state.get(FERTILE);
		if(fertile == 0) {
			return;
		}

		if (world.random.nextInt(2) == 0) {
			Integer egg_count = state.get(EGG_COUNT);

			egg_count--;
			fertile--;
			if(fertile < 0)
				fertile = 0;
			if(egg_count < 0)
				egg_count = 0;

			if(fertile > egg_count) {
				fertile = egg_count;
			}

			world.setBlockState(pos, state.with(EGG_COUNT, egg_count).with(FERTILE, fertile));
			world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
			ChickenEntity chickenEntity = EntityType.CHICKEN.create(world);
			if(chickenEntity != null) {
				chickenEntity.setBaby(true);
				chickenEntity.refreshPositionAndAngles((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.3, 0.0F, 0.0F);
				world.spawnEntity(chickenEntity);
			}
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(world.isClient()) {
			return ActionResult.SUCCESS;
		}
		Integer egg_count = state.get(EGG_COUNT);
		egg_count--;
		if(SetEggCount(egg_count, world, pos))  {
			player.giveItemStack(new ItemStack(Items.EGG, 1));
			return ActionResult.SUCCESS;
		}
		else
			return ActionResult.FAIL;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
			BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return !state.canPlaceAt(world, pos) ?
				Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return sideCoversSmallSquare(world, pos.down(), Direction.UP) || world.getBlockState(pos.down()).isIn(HHBlockTags.VALID_NEST_PLACEMENT);
	}


	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.25f, 1.0f);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ChickenNestBlockEntity(pos, state);
	}
}
