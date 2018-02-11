package me.arcanox.techmod.common.blocks

import me.arcanox.techmod.api.API
import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.common.blocks.base.BlockBase
import me.arcanox.techmod.common.tileentities.TileEntityAutomaticDoor
import me.arcanox.techmod.util.reflect.HasItemBlock
import me.arcanox.techmod.util.reflect.HasItemModel
import me.arcanox.techmod.util.reflect.ModBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockDoor
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

@ModBlock
@HasItemBlock
@HasItemModel
object BlockAutomaticDoor : BlockBase(Constants.Blocks.AutomaticDoor, Material.GLASS) {
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
		setLightOpacity(0);
		setHardness(4.0f);
		setHarvestLevel("pickaxe", Blocks.IRON_DOOR.getHarvestLevel(Blocks.IRON_DOOR.defaultState));
		
		defaultState = this.blockState.baseState
			.withProperty(BlockHorizontal.FACING, EnumFacing.WEST)
			.withProperty(BlockDoor.OPEN, false)
			.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
			.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT);
	}
	
	// region State
	
	override fun createBlockState(): BlockStateContainer = BlockStateContainer(this,
	                                                                           BlockHorizontal.FACING,
	                                                                           BlockDoor.OPEN,
	                                                                           BlockDoor.HALF,
	                                                                           BlockDoor.HINGE);
	
	override fun getStateFromMeta(meta: Int): IBlockState {
		val hinge = (meta and 0b1_0_00) shr 3;
		val half = ((meta and 0b0_1_00)) shr 2;
		val facing = meta and 0b0_0_11;
		
		return this.defaultState
			.withProperty(BlockHorizontal.FACING, EnumFacing.HORIZONTALS[facing])
			.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.values()[half])
			.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.values()[hinge]);
	}
	
	override fun getMetaFromState(state: IBlockState): Int {
		val facing = EnumFacing.HORIZONTALS.indexOf(state.getValue(BlockHorizontal.FACING));
		val half = state.getValue(BlockDoor.HALF).ordinal;
		val hinge = state.getValue(BlockDoor.HINGE).ordinal;
		
		return (facing) +
		       (half shl 2) +
		       (hinge shl 3);
	}
	
	override fun getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
		val half = state.getValue(BlockDoor.HALF);
		
		return when (half) {
			BlockDoor.EnumDoorHalf.LOWER -> {
				val te = this.getTileEntitySafe(world, pos, state) as TileEntityAutomaticDoor;
				state.withProperty(BlockDoor.OPEN, te.open);
			}
			else                         -> {
				val belowState = world.getBlockState(pos.down());
				
				if (belowState.block === this)
					state.withProperty(BlockDoor.OPEN, belowState.getValue(BlockDoor.OPEN));
				else
					state;
			}
		}
	}
	
	// endregion
	
	// region Rendering/Lighting
	
	override fun isFullCube(state: IBlockState?): Boolean = false
	
	override fun isNormalCube(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Boolean = false
	
	override fun isPassable(world: IBlockAccess, pos: BlockPos): Boolean = world.getBlockState(pos).getActualState(world, pos).getValue(BlockDoor.OPEN)
	
	override fun isOpaqueCube(state: IBlockState?): Boolean = false
	
	override fun doesSideBlockRendering(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?): Boolean = false
	
	override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean = false /*{
		if (layer != BlockRenderLayer.CUTOUT) return false;
		
		val half = state.getValue(BlockDoor.HALF);
		
		return half == BlockDoor.EnumDoorHalf.LOWER;
	}*/
	
	// endregion
	
	// region Interaction
	
	override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
//		if (world.isRemote) return true;
//
//		val half = state.getValue(BlockDoor.HALF);
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
		return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}
	
	// endregion
	
	// region AABB
	
	/*
//	override fun getSelectedBoundingBox(state: IBlockState, world: World, pos: BlockPos): AxisAlignedBB {
//		if (!world.isRemote) return super.getSelectedBoundingBox(state, world, pos);
//
//		val box = this.getBoundingBox(state, world, pos).offset(pos);
//		return box;
//		val half = state.getValue(BlockDoor.HALF);
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
		val half = state.getValue(BlockDoor.HALF);
		val facing = state.getValue(BlockHorizontal.FACING);
		val bbSide = when {
			open -> when (hinge) {
				BlockDoor.EnumHingePosition.LEFT -> facing.rotateY()
				else                             -> facing.rotateYCCW()
			}
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
				val oX = pFacing.frontOffsetX;
				val oZ = pFacing.frontOffsetZ;
				
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
		
		val placedHalf = state.getValue(BlockDoor.HALF);
		
		if (placedHalf == BlockDoor.EnumDoorHalf.UPPER) return;
		
		world.setBlockState(pos.up(), state.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER));
	}
	
	// endregion
	
	// region Removing
	
	override fun getMobilityFlag(state: IBlockState?): EnumPushReaction = EnumPushReaction.DESTROY
	
	override fun getItemDropped(state: IBlockState, rand: Random?, fortune: Int): Item {
		return when (state.getValue(BlockDoor.HALF)) {
			BlockDoor.EnumDoorHalf.LOWER -> API.blocks().getBlockItem(Constants.Blocks.AutomaticDoor)
			else                         -> Items.AIR
		}
	}
	
	override fun onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
		val below = pos.down();
		val above = pos.up();
		val half = state.getValue(BlockDoor.HALF);
		
		when (half) {
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
		val half = state.getValue(BlockDoor.HALF);
		
		when (half) {
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
	
	override fun hasTileEntity(state: IBlockState): Boolean = state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER
	
	override fun createTileEntity(world: World, state: IBlockState): TileEntity = TileEntityAutomaticDoor().also {
		it.open = state.getValue(BlockDoor.OPEN);
	}
	
	// endregion
}