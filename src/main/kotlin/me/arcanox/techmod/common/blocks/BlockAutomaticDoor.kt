package me.arcanox.techmod.common.blocks

import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.util.reflect.HasItemBlock
import me.arcanox.techmod.util.reflect.HasItemModel
import me.arcanox.techmod.util.reflect.ModBlock
import me.arcanox.techmod.common.blocks.base.BlockBase
import me.arcanox.techmod.common.tileentities.TileEntityAutomaticDoor
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@ModBlock
@HasItemBlock
@HasItemModel
object BlockAutomaticDoor : BlockBase(Constants.Blocks.AutomaticDoor, Material.IRON) {
	private val CollisionBoxWest = AxisAlignedBB(
			0.0, 0.0, 0.0,
			4.0 / 16.0, 1.0, 1.0)
	private val CollisionBoxNorth = AxisAlignedBB(
			0.0, 0.0, 0.0,
			1.0, 1.0, 4.0 / 16.0)
	private val CollisionBoxEast = AxisAlignedBB(
			12.0 / 16.0, 0.0, 0.0,
			1.0, 1.0, 1.0)
	private val CollisionBoxSouth = AxisAlignedBB(
			0.0, 0.0, 12.0 / 16.0,
			1.0, 1.0, 1.0)
	
	init {
		defaultState = this.blockState.baseState
				.withProperty(BlockHorizontal.FACING, EnumFacing.WEST);
	}
	
	override fun doesSideBlockRendering(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?): Boolean = false
	
	override fun isFullCube(state: IBlockState?): Boolean = false
	
	override fun isPassable(worldIn: IBlockAccess?, pos: BlockPos?): Boolean = false
	
	override fun getBlockLayer(): BlockRenderLayer = BlockRenderLayer.CUTOUT
	
	override fun createBlockState(): BlockStateContainer
			= BlockStateContainer(this, BlockHorizontal.FACING);
	
	override fun getBoundingBox(blockState: IBlockState?, worldIn: IBlockAccess?, pos: BlockPos?): AxisAlignedBB {
		val facing = blockState?.getValue(BlockHorizontal.FACING);
		
		return when (facing) {
			EnumFacing.NORTH -> CollisionBoxNorth
			EnumFacing.EAST  -> CollisionBoxEast
			EnumFacing.SOUTH -> CollisionBoxSouth
			else             -> CollisionBoxWest
		}
	}
	
	override fun getStateFromMeta(meta: Int): IBlockState {
		val facing = meta and 0b11;
		
		return this.blockState.baseState.withProperty(BlockHorizontal.FACING, EnumFacing.HORIZONTALS[facing]);
	}
	
	override fun getMetaFromState(state: IBlockState): Int {
		val facing = EnumFacing.HORIZONTALS.indexOf(state.getValue(BlockHorizontal.FACING));
		
		return facing;
	}
	
	override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
		return super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
	}
	
	override fun getStateForPlacement(world: World?, pos: BlockPos?, blockSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?, hand: EnumHand?): IBlockState {
		if (placer == null) return this.defaultState;
		
		return this.blockState.baseState
				.withProperty(BlockHorizontal.FACING, placer.horizontalFacing.opposite);
	}
	
	override fun hasTileEntity(state: IBlockState?): Boolean = true
	
	override fun createTileEntity(world: World?, state: IBlockState?): TileEntity = TileEntityAutomaticDoor()
}