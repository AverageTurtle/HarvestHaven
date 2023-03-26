package com.averageturtle.harvest_haven.block;

import com.averageturtle.harvest_haven.HarvestHaven;
import com.averageturtle.harvest_haven.block.entity.CookingPotBlockEntity;
import com.averageturtle.harvest_haven.item.HHItems;
import com.averageturtle.harvest_haven.recipe.CookingPotRecipe;
import com.averageturtle.harvest_haven.recipe.HHIngredient.HHItemIngredient;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class CookingPot extends BlockWithEntity  {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

	public static Boolean AttemptCraft(World world, CookingPotBlockEntity blockEntity) {
		{
			int empty = -1;
			for(int i = 0; i < CookingPotBlockEntity.INVENTORY_SIZE; i++) {
				ItemStack stack = blockEntity.getStack(i);
				if(stack.isEmpty()) {
					empty = i;
					break;
				}
			}
			if(empty < 0)
				return false;
		}
		Optional<CookingPotRecipe> match = world.getRecipeManager().getFirstMatch(HarvestHaven.COOKING_POT_RECIPE_TYPE, blockEntity, world);
		if(match.isPresent()) {
			CookingPotRecipe recipe = match.get();
			for (HHItemIngredient ingredient : recipe.input) {
				for(int i = 0; i < CookingPotBlockEntity.INVENTORY_SIZE; i++) {
					if(ingredient.ingredient().test(blockEntity.getStack(i)))
						blockEntity.removeStack(i, ingredient.count());
				}
			}


			//TODO Stacking of results for stackable items
			int empty = -1;
			for(int i = 0; i < CookingPotBlockEntity.INVENTORY_SIZE; i++) {
				ItemStack stack = blockEntity.getStack(i);
				if(stack.isEmpty()) {
					empty = i;
					break;
				}
			}
			assert empty < 0;

			blockEntity.setStack(empty, recipe.getResult(world.getRegistryManager()).copy());
			return true;
		}

		return false;
	}
	protected CookingPot(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return (world1, pos, state1, blockEntity) -> ((CookingPotBlockEntity)blockEntity).tick();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CookingPotBlockEntity(pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	//TODO (Sam) Figure out why these are deprecated
	@SuppressWarnings("deprecation")
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(world.isClient()) {
			return ActionResult.SUCCESS;
		}

		ItemStack playerStack = player.getMainHandStack();
		CookingPotBlockEntity blockEntity = (CookingPotBlockEntity)world.getBlockEntity(pos);
		if(blockEntity == null)
			return ActionResult.PASS;


		if(playerStack.isEmpty()) {
			for(int i = 0; i < CookingPotBlockEntity.INVENTORY_SIZE; i++) {
				int slot = (CookingPotBlockEntity.INVENTORY_SIZE-1)-i;

				ItemStack stack = blockEntity.getStack(slot);
				if(!stack.isEmpty()) {
					blockEntity.removeStack(slot);
					player.giveItemStack(stack);
					return ActionResult.SUCCESS;
				}
			}
			return ActionResult.PASS;
		}

		if(playerStack.isOf(HHItems.WOODEN_MIXING_SPOON)) {
			AttemptCraft(world, blockEntity);
			return ActionResult.SUCCESS;
		}


		int empty = -1;
		for(int i = 0; i < CookingPotBlockEntity.INVENTORY_SIZE; i++) {
			ItemStack stack = blockEntity.getStack(i);
			if(stack.isEmpty()) {
				empty = i;
				break;
			}
		}
		if(empty < 0)
			return ActionResult.PASS;

		player.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		blockEntity.setStack(empty, playerStack);
		return ActionResult.SUCCESS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(0.125f, 0f, 0.125f, 0.875f, 0.625f, 0.875f);
	}
	@SuppressWarnings("deprecation")
	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}
}
