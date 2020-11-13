package me.arcanox.techmod.common.blocks

import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.common.tileentities.AutomaticDoorTileEntity
import me.arcanox.techmod.util.reflect.HasBlockItem
import me.arcanox.techmod.util.reflect.HasItemModel
import me.arcanox.techmod.util.reflect.ModBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.material.Material
import net.minecraft.block.material.PushReaction
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.pathfinding.PathType
import net.minecraft.state.StateContainer
import net.minecraft.state.properties.BlockStateProperties.*
import net.minecraft.state.properties.DoorHingeSide
import net.minecraft.state.properties.DoubleBlockHalf
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ActionResultType
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.shapes.ISelectionContext
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorld
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import net.minecraftforge.common.ToolType
import net.minecraftforge.common.util.Constants.BlockFlags
import net.minecraftforge.common.util.Constants.WorldEvents

@ModBlock
@HasBlockItem
@HasItemModel
object AutomaticDoorBlock : BlockBase(
	Constants.Blocks.AutomaticDoor,
	Properties.create(Material.GLASS)
		.hardnessAndResistance(4.0f)
		.harvestTool(ToolType.PICKAXE)
		.harvestLevel(Blocks.IRON_DOOR.getHarvestLevel(Blocks.IRON_DOOR.defaultState))
		.notSolid()
) {
	// region Constants
	
	private const val FrameThickness = 2.0;
	private const val MountWidth = 6.0;
	private const val MountHeight = 1.0;
	
	private val MountNorthShape = makeCuboidShape(0.0, 16.0 - MountHeight, 0.0, 16.0, 16.0, MountWidth)
	private val MountSouthShape = makeCuboidShape(0.0, 16.0 - MountHeight, 16.0 - MountWidth, 16.0, 16.0, 16.0)
	private val MountEastShape = makeCuboidShape(16.0 - MountWidth, 16.0 - MountHeight, 0.0, 16.0, 16.0, 16.0)
	private val MountWestShape = makeCuboidShape(0.0, 16.0 - MountHeight, 0.0, MountWidth, 16.0, 16.0)
	private val FullNorthShape = makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, FrameThickness)
	private val FullSouthShape = makeCuboidShape(0.0, 0.0, 16.0 - FrameThickness, 16.0, 16.0, 16.0)
	private val FullEastShape = makeCuboidShape(16.0 - FrameThickness, 0.0, 0.0, 16.0, 16.0, 16.0)
	private val FullWestShape = makeCuboidShape(0.0, 0.0, 0.0, FrameThickness, 16.0, 16.0)
	private val PartialNorthShape = makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0 - MountHeight, FrameThickness)
	private val PartialSouthShape = makeCuboidShape(0.0, 0.0, 16.0 - FrameThickness, 16.0, 16.0 - MountHeight, 16.0)
	private val PartialEastShape = makeCuboidShape(16.0 - FrameThickness, 0.0, 0.0, 16.0, 16.0 - MountHeight, 16.0)
	private val PartialWestShape = makeCuboidShape(0.0, 0.0, 0.0, FrameThickness, 16.0 - MountHeight, 16.0)
	
	// endregion
	
	init {
		defaultState = this.stateContainer.baseState
			.with(HORIZONTAL_FACING, Direction.WEST)
			.with(OPEN, false)
			.with(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
			.with(DOOR_HINGE, DoorHingeSide.LEFT);
	}
	
	// region State
	
	override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
		builder.add(HORIZONTAL_FACING, OPEN, DOUBLE_BLOCK_HALF, DOOR_HINGE);
	}
	
	override fun updatePostPlacement(stateIn: BlockState, facing: Direction, facingState: BlockState, world: IWorld, currentPos: BlockPos, facingPos: BlockPos): BlockState = stateIn.let {
		val half = it.get(DOUBLE_BLOCK_HALF);
		
		when (half) {
			DoubleBlockHalf.LOWER -> {
				val te = this.getTileEntitySafe(world, currentPos, it) as AutomaticDoorTileEntity;
				
				it.with(OPEN, te.open);
			}
			DoubleBlockHalf.UPPER -> {
				val belowState = world.getBlockState(currentPos.down());
				
				if (belowState.block === this)
					it.with(OPEN, belowState.get(OPEN));
				else
					it;
			}
			else -> it
		}
	}
	
	// endregion
	
	// region Rendering/Lighting
	
	override fun allowsMovement(state: BlockState, worldIn: IBlockReader, pos: BlockPos, type: PathType): Boolean = when (type) {
		PathType.LAND,
		PathType.AIR   -> state.get(OPEN);
		PathType.WATER -> false;
	}
	
	override fun isSideInvisible(state: BlockState, adjacentBlockState: BlockState, side: Direction): Boolean = false
	
	// endregion
	
	// region Interaction
	
	override fun onBlockActivated(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, rayTraceResult: BlockRayTraceResult): ActionResultType {
		// TODO: Open some sort of GUI later
		return ActionResultType.PASS;
	}
	
	// endregion
	
	// region AABB
	
	override fun getShape(state: BlockState, worldIn: IBlockReader, pos: BlockPos, context: ISelectionContext): VoxelShape {
		val shapeFacing = state.get(HORIZONTAL_FACING).opposite; // Minecraft doors "face" the opposite side from the one that they're flush with
		val open = state.get(OPEN);
		val hinge = state.get(DOOR_HINGE);
		val openFacing = when (hinge) {
			DoorHingeSide.LEFT -> shapeFacing.rotateY()
			else -> shapeFacing.rotateYCCW()
		};
		val effectiveFacing = if (open) openFacing else shapeFacing;
		
		return when (effectiveFacing) {
			Direction.NORTH -> FullNorthShape;
			Direction.SOUTH -> FullSouthShape;
			Direction.WEST -> FullWestShape;
			else -> FullEastShape;
		};
	}
	
	// endregion
	
	// region Placing
	
	override fun isValidPosition(state: BlockState, world: IWorldReader, pos: BlockPos): Boolean {
		if (pos.y >= world.height - 1) return false;
		
		val posBelow = pos.down();
		val stateBelow = world.getBlockState(posBelow);
		
		return when (state.get(DOUBLE_BLOCK_HALF)) {
			DoubleBlockHalf.LOWER -> stateBelow.isSolidSide(world, posBelow, Direction.UP);
			else -> state.block == this;
		}
	}
	
	override fun getStateForPlacement(context: BlockItemUseContext): BlockState? {
		val world = context.world;
		val pos = context.pos;
		
		if (pos.y >= world.height - 1 || !world.getBlockState(pos.up()).isReplaceable(context))
			return null;
		
		return this.defaultState
			.with(HORIZONTAL_FACING, context.placementHorizontalFacing)
			.with(DOOR_HINGE, this.getHingeSide(context))
			.with(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
	}
	
	override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
		worldIn.setBlockState(pos.up(), state.with(DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
	}
	
	/**
	 * Derived from DoorBlock.getHingeSide
	 */
	fun getHingeSide(context: BlockItemUseContext): DoorHingeSide {
		val world = context.world;
		val pos = context.pos;
		val facing = context.placementHorizontalFacing;
		val posUp = pos.up();
		val dirLeft = facing.rotateYCCW();
		val dirRight = facing.rotateY();
		val posLeft = pos.offset(dirLeft);
		val posUpLeft = posUp.offset(dirLeft);
		val posRight = pos.offset(dirRight);
		val posUpRight = posUp.offset(dirRight);
		val stateLeft = world.getBlockState(posLeft);
		val stateUpLeft = world.getBlockState(posUpLeft);
		val stateRight = world.getBlockState(posRight);
		val stateUpRight = world.getBlockState(posUpRight);
		val doorToLeft = stateLeft.block == this && stateLeft.get(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
		val doorToRight = stateRight.block == this && stateRight.get(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
		
		// Bias towards the side with the most adjacent solid faces
		val adjacentBlockBias = (if (stateLeft.isOpaqueCube(world, posLeft)) -1 else 0) +
		                        (if (stateUpLeft.isOpaqueCube(world, posUpLeft)) -1 else 0) +
		                        (if (stateRight.isOpaqueCube(world, posRight)) 1 else 0) +
		                        (if (stateUpRight.isOpaqueCube(world, posUpRight)) 1 else 0);
		
		return if ((!doorToLeft || doorToRight) && adjacentBlockBias <= 0) {
			if ((!doorToRight || doorToLeft) && adjacentBlockBias >= 0) {
				val xOffset = facing.xOffset;
				val zOffset = facing.zOffset;
				val hitVec = context.hitVec;
				val hitOffsetX = hitVec.x - pos.x.toDouble();
				val hitOffsetZ = hitVec.z - pos.z.toDouble();
				
				if ((xOffset >= 0 || hitOffsetZ >= 0.5) &&
				    (xOffset <= 0 || hitOffsetZ <= 0.5) &&
				    (zOffset >= 0 || hitOffsetX <= 0.5) &&
				    (zOffset <= 0 || hitOffsetX >= 0.5)) {
					DoorHingeSide.LEFT;
				} else {
					DoorHingeSide.RIGHT;
				}
			} else {
				DoorHingeSide.LEFT;
			}
		} else {
			DoorHingeSide.RIGHT;
		}
	}
	
	// endregion
	
	// region Removing
	
	override fun getPushReaction(state: BlockState?) = PushReaction.BLOCK
	
	override fun harvestBlock(worldIn: World, player: PlayerEntity, pos: BlockPos, state: BlockState, te: TileEntity?, stack: ItemStack) {
		super.harvestBlock(worldIn, player, pos, Blocks.AIR.defaultState, te, stack)
	}
	
	override fun onBlockHarvested(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
		val half = state.get(DOUBLE_BLOCK_HALF);
		val otherHalfPos = when (half) {
			DoubleBlockHalf.LOWER -> pos.up();
			else -> pos.down();
		};
		val otherHalfState = world.getBlockState(otherHalfPos);
		
		if (otherHalfState.block == this && otherHalfState.get(DOUBLE_BLOCK_HALF) != half) {
			world.setBlockState(otherHalfPos, Blocks.AIR.defaultState, BlockFlags.DEFAULT or BlockFlags.NO_NEIGHBOR_DROPS);
			world.playEvent(player, WorldEvents.BREAK_BLOCK_EFFECTS, otherHalfPos, getStateId(otherHalfState));
			
			if (!world.isRemote && !player.isCreative && player.func_234569_d_(otherHalfState)) {
				val handStack = player.heldItemMainhand;
				
				spawnDrops(state, world, pos, null, player, handStack);
				spawnDrops(otherHalfState, world, otherHalfPos, null, player, handStack);
			}
		}
		
		super.onBlockHarvested(world, pos, state, player);
	}
	
	// endregion
	
	// region Tile Entity
	
	override fun hasTileEntity(state: BlockState?): Boolean = state?.get(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
	
	override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? = AutomaticDoorTileEntity().also {
		it.open = state?.get(OPEN) ?: false;
	}
	
	// endregion
}