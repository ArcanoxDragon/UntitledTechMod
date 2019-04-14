package me.arcanox.techmod.common.tileentities

import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.common.blocks.BlockAutomaticDoor
import me.arcanox.techmod.util.reflect.HasTESR
import me.arcanox.techmod.util.reflect.ModTileEntity
import me.arcanox.techmod.util.toVec3d
import net.minecraft.block.Block
import net.minecraft.block.BlockDoor
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@ModTileEntity(Constants.Blocks.AutomaticDoor)
@HasTESR("TESRAutomaticDoor")
class TileEntityAutomaticDoor : TileEntityBase(), ITickable {
	companion object {
		const val TicksToOpen = 15;
		const val Range = 4; // blocks // TODO: make this configurable later
	}
	
	private var animTicks = 0;
	private var readingNbt = false;
	private val checkAABB = Range.toDouble().let { AxisAlignedBB(-it, -it, -it, it, it, it) };
	
	var open: Boolean = false
		set(value) {
			if (this.readingNbt || this.animTicks == 0 || this.animTicks == TicksToOpen) field = value;
		}
	
	fun getRotation(partialTicks: Float = 0.0f): Float {
		val fTicks = when {
			this.open -> this.animTicks + partialTicks
			else      -> this.animTicks - partialTicks
		};
		val progress = Math.max(Math.min(fTicks / TicksToOpen.toFloat(), 1.0f), 0.0f);
		val eased = (Math.cos(progress.toDouble() * Math.PI + Math.PI) + 1.0f).toFloat() / 2.0f;
		return 90.0f * eased;
	}
	
	override fun getRenderBoundingBox(): AxisAlignedBB = super.getRenderBoundingBox().expand(0.0, 1.0, 0.0)
	
	override fun getBlockType(): Block = BlockAutomaticDoor
	
	@SideOnly(Side.CLIENT)
	override fun hasFastRenderer(): Boolean = false
	
	override fun update() {
		if (this.open && this.animTicks < TicksToOpen) this.animTicks++;
		if (!this.open && this.animTicks > 0) this.animTicks--;
		
		if (this.world.isRemote) return;
		
		if (world.getBlockState(this.pos).block !== this.getBlockType()) return; // wtf
		
		val canChangeState = this.animTicks == 0 || this.animTicks == TicksToOpen;
		
		if (canChangeState) {
			val state = this.world.getBlockState(pos);
			val middleDoor = this.pos.toVec3d().add(0.5, 1.0, 0.5);
			val hasDoorNeighbor = arrayOf(this.pos.north(),
			                              this.pos.south(),
			                              this.pos.east(),
			                              this.pos.west()).any { world.getBlockState(it).block === this.getBlockType() };
			val checkPoint = when {
				hasDoorNeighbor -> {
					val facing = state.getValue(BlockHorizontal.FACING);
					val hinge = state.getValue(BlockDoor.HINGE);
					val offsetDir = when (hinge) {
						BlockDoor.EnumHingePosition.LEFT -> facing.rotateYCCW()
						else                             -> facing.rotateY()
					}
					
					middleDoor.add(offsetDir.xOffset * 0.5, 0.0, offsetDir.zOffset * 0.5);
				}
				else            -> middleDoor
			}
			val entities = this.world.getEntitiesWithinAABB(EntityPlayer::class.java, this.checkAABB.offset(checkPoint));
			val shouldBeOpen = entities.any();
			
			if (shouldBeOpen != this.open) {
				this.open = shouldBeOpen;
				this.world.setBlockState(this.pos, state.withProperty(BlockDoor.OPEN, this.open));
			}
		}
	}
	
	override fun getUpdateTag(): NBTTagCompound = super.getUpdateTag().also {
		it.setBoolean("open", this.open)
	}
	
	override fun handleUpdateTag(tag: NBTTagCompound) {
		super.handleUpdateTag(tag);
		this.readingNbt = true;
		
		this.open = tag.getBoolean("open");
		
		this.readingNbt = false;
	}
	
	override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean = oldState.block !== newState.block;
}