package me.arcanox.techmod.common.blocks

import me.arcanox.techmod.api.API
import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.common.tileentities.TileEntityAutomaticDoor
import me.arcanox.techmod.util.reflect.HasBlockItem
import me.arcanox.techmod.util.reflect.HasItemModel
import me.arcanox.techmod.util.reflect.ModBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.material.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
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
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorld
import net.minecraft.world.World
import net.minecraftforge.common.ToolType
import java.util.*

@ModBlock
@HasBlockItem
@HasItemModel
object BlockAutomaticDoor : BlockBase(
	Constants.Blocks.AutomaticDoor,
	Block.Properties
		.create(Material.GLASS)
		.hardnessAndResistance(4.0f)
		.harvestTool(ToolType.PICKAXE)
		.harvestLevel(Blocks.IRON_DOOR.getHarvestLevel(Blocks.IRON_DOOR.defaultState))
) {
	// region Constants
	
	private const val FrameThickness = 2.0 / 16.0;
	private const val MountWidth = 6.0 / 16.0;
	private const val MountHeight = 1.0 / 16.0;
	
	private val CollisionBoxDoorWest = AxisAlignedBB(0.0, 0.0, 0.0,
	                                                 FrameThickness, 1.0, 1.0)
	private val CollisionBoxDoorNorth = AxisAlignedBB(0.0, 0.0, 0.0,
	                                                  1.0, 1.0, FrameThickness)
	private val CollisionBoxDoorEast = AxisAlignedBB(1.0 - FrameThickness, 0.0, 0.0,
	                                                 1.0, 1.0, 1.0)
	private val CollisionBoxDoorSouth = AxisAlignedBB(0.0, 0.0, 1.0 - FrameThickness,
	                                                  1.0, 1.0, 1.0)
	
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
				val te = this.getTileEntitySafe(world, currentPos, it) as TileEntityAutomaticDoor;
				
				it.with(OPEN, te.open);
			}
			DoubleBlockHalf.UPPER -> {
				val belowState = world.getBlockState(currentPos.down());
				
				if (belowState.block === this)
					it.with(OPEN, belowState.get(OPEN));
				else
					it;
			}
			else                  -> it
		}
	}
	
	// endregion
	
	// region Rendering/Lighting
	
	override fun allowsMovement(state: BlockState, worldIn: IBlockReader, pos: BlockPos, type: PathType): Boolean = when (type) {
		PathType.LAND  -> state.get(OPEN);
		PathType.AIR   -> state.get(OPEN);
		PathType.WATER -> false;
	}
	
	override fun isSideInvisible(state: BlockState, adjacentBlockState: BlockState, side: Direction): Boolean = false
	
	// endregion
	
	// region Interaction
	
	override fun onBlockActivated(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, rayTraceResult: BlockRayTraceResult): ActionResultType {
//		if (world.isRemote) return true;
//
//		val half = state.getValue(DOUBLE_BLOCK_HALF);
//
//		return when (half) {
//			BlockDoor.EnumDoorHalf.UPPER -> {
//				val below = pos.down();
//				val belowState = world.getBlockState(below);
//
//				if (belowState.block !== this) false;
//				else belowState.block.onBlockActivated(world, below, belowState, player, hand, facing, hitX, hitY, hitZ);
//			}
//			else                         -> {
//				val te = this.getTileEntitySafe(world, pos, state) as TileEntityAutomaticDoor;
//
//				te.open = !te.open;
//				world.setBlockState(pos, state.withProperty(BlockDoor.OPEN, te.open), 3);
//
//				true;
//			}
//		}
		
		// TODO: Open some sort of GUI later
		return super.onBlockActivated(state, world, pos, player, handIn, rayTraceResult);
	}
	
	// endregion
	
	// region AABB
	
	/*
//	override fun getSelectedBoundingBox(state: IBlockState, world: World, pos: BlockPos): AxisAlignedBB {
//		if (!world.isRemote) return super.getSelectedBoundingBox(state, world, pos);
//
//		val box = this.getBoundingBox(state, world, pos).offset(pos);
//		return box;
//		val half = state.getValue(DOUBLE_BLOCK_HALF);
//		var partHit = SelectedAABB.Frame;
//
//		val result = when (half) {
//			BlockDoor.EnumDoorHalf.LOWER -> box
//			else                         -> {
//				val mc = Minecraft.getMinecraft();
//				val player = mc.player;
//				val reach = mc.playerController.blockReachDistance;
//				val adjDir = state.getValue(BlockHorizontal.FACING).opposite;
//				val fVec = adjDir.directionVec;
//				val adjV = 1.0 - MountHeight;
//				val adjH = MountWidth - FrameThickness;
//				val boxWhenClosed = this.getBoundingBox(state.withProperty(BlockDoor.OPEN, false), world, pos)
//					.offset(pos)
//					.contract(0.0, -adjV, 0.0)
//					.addCoord(adjH * fVec.x, 0.0, adjH * fVec.z);
//
//				this.setAABBForSelection(Block.FULL_BLOCK_AABB);
//				val trace = player.rayTrace(reach.toDouble(), 1.0f);
//				this.setAABBForSelection(null);
//
//				when (trace) {
//					null -> this.selectedBB.first
//					else -> {
//						if (trace.typeOfHit === RayTraceResult.Type.MISS)
//							this.selectedBB.first;
//
//						val bPos = Vec3d(trace.blockPos.x.toDouble(), trace.blockPos.y.toDouble(), trace.blockPos.z.toDouble());
//						val subHit = trace.hitVec.subtract(bPos);
//
//						if (subHit.yCoord >= adjV || trace.sideHit == EnumFacing.DOWN) {
//							partHit = SelectedAABB.Mount;
//
//							boxWhenClosed;
//						} else {
//							box.contract(0.0, MountHeight, 0.0);
//						}
//					}
//				}
//			}
//		}
//
//		this.selectedBB = Pair(result, partHit);
//
//		return result;
//	}
*/
	
	override fun getBoundingBox(stateIn: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB {
		val state = stateIn.getActualState(world, pos);
		val open = state.getValue(BlockDoor.OPEN);
		val hinge = state.getValue(BlockDoor.HINGE);
		val half = state.getValue(DOUBLE_BLOCK_HALF);
		val facing = state.getValue(BlockHorizontal.FACING);
		val hingeSide = when (hinge) {
			BlockDoor.EnumHingePosition.LEFT -> facing.rotateY()
			else                             -> facing.rotateYCCW()
		};
		val bbSide = when {
			open -> hingeSide
			else -> facing
		};
		val rotatedBox = when (bbSide) {
			EnumFacing.NORTH -> CollisionBoxDoorNorth
			EnumFacing.EAST  -> CollisionBoxDoorEast
			EnumFacing.SOUTH -> CollisionBoxDoorSouth
			else             -> CollisionBoxDoorWest
		}
		
		return when (half) {
			BlockDoor.EnumDoorHalf.LOWER -> rotatedBox.expand(0.0, 1.0 - MountHeight, 0.0)
			else                         -> rotatedBox.expand(0.0, -1.0, 0.0).contract(0.0, MountHeight, 0.0)
		}
	}
	
	// endregion
	
	// region Placing
	
	override fun canPlaceBlockAt(world: World, pos: BlockPos): Boolean {
		if (pos.y >= world.height - 1) return false;
		return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) &&
		       super.canPlaceBlockAt(world, pos) &&
		       super.canPlaceBlockAt(world, pos.up());
	}
	
	override fun getStateForPlacement(world: World?, pos: BlockPos?, blockSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?, hand: EnumHand?): IBlockState {
		if (placer == null) return this.defaultState;
		
		val pFacing = placer.horizontalFacing;
		val facing = pFacing.opposite;
		val hingeSide = when (blockSide) {
			EnumFacing.UP,
			EnumFacing.DOWN  -> {
				//val oX = pFacing.frontOffsetX;
				//val oZ = pFacing.frontOffsetZ;
				
				BlockDoor.EnumHingePosition.LEFT;
			}
			facing.rotateY() -> BlockDoor.EnumHingePosition.RIGHT
			else             -> BlockDoor.EnumHingePosition.LEFT
		};
		
		return this.defaultState
			.withProperty(BlockHorizontal.FACING, facing)
			.withProperty(BlockDoor.HINGE, hingeSide);
	}
	
	override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
		if (world.isRemote) return;
		
		val placedHalf = state.getValue(DOUBLE_BLOCK_HALF);
		
		if (placedHalf == BlockDoor.EnumDoorHalf.UPPER) return;
		
		world.setBlockState(pos.up(), state.withProperty(DOUBLE_BLOCK_HALF, BlockDoor.EnumDoorHalf.UPPER));
	}
	
	// endregion
	
	// region Removing
	
	override fun getPushReaction(state: IBlockState?) = EnumPushReaction.DESTROY
	
	override fun getItemDropped(state: IBlockState, rand: Random?, fortune: Int): Item {
		return when (state.getValue(DOUBLE_BLOCK_HALF)) {
			BlockDoor.EnumDoorHalf.LOWER -> API.getInstance().blocks().getBlockItem(Constants.Blocks.AutomaticDoor)
			else                         -> Items.AIR
		}
	}
	
	override fun onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
		val below = pos.down();
		val above = pos.up();
		
		when (state.getValue(DOUBLE_BLOCK_HALF)) {
			BlockDoor.EnumDoorHalf.UPPER -> {
				if (world.getBlockState(below).block === this) {
					if (player.capabilities.isCreativeMode)
						world.setBlockToAir(below); // set below to air
				}
			}
			else                         -> {
				if (world.getBlockState(above).block === this) {
					if (player.capabilities.isCreativeMode)
						world.setBlockToAir(pos); // set me to air
					
					world.setBlockToAir(above); // set upper to air
				}
			}
		}
	}
	
	// endregion
	
	// region Updates
	
	override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, blockChanged: Block, fromPos: BlockPos) {
		when (state.getValue(DOUBLE_BLOCK_HALF)) {
			BlockDoor.EnumDoorHalf.UPPER -> {
				val below = pos.down();
				val belowState = world.getBlockState(below);
				
				if (belowState.block !== this) {
					world.setBlockToAir(pos);
				} else if (blockChanged !== this) {
					belowState.neighborChanged(world, below, blockChanged, fromPos);
				}
			}
			else                         -> {
				var didRemove = false;
				val above = pos.up();
				val aboveState = world.getBlockState(above);
				
				if (aboveState.block !== this) {
					world.setBlockToAir(pos);
					didRemove = true;
				}
				
				if (!world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP)) {
					world.setBlockToAir(pos);
					didRemove = true;
					
					if (aboveState.block === this) {
						world.setBlockToAir(above);
					}
				}
				
				if (didRemove) {
					if (!world.isRemote) {
						this.dropBlockAsItem(world, pos, state, 0);
					}
				}
			}
		}
	}
	
	// endregion
	
	// region Tile Entity
	
	override fun hasTileEntity(state: IBlockState): Boolean = state.getValue(DOUBLE_BLOCK_HALF) == BlockDoor.EnumDoorHalf.LOWER
	
	override fun createTileEntity(world: World, state: IBlockState): TileEntity = TileEntityAutomaticDoor().also {
		it.open = state.getValue(BlockDoor.OPEN);
	}
	
	// endregion
}