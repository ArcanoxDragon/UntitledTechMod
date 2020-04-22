package me.arcanox.techmod.common.tileentities

import com.google.common.base.Predicates
import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.client.tileentities.renderers.AutomaticDoorTileRenderer
import me.arcanox.techmod.common.blocks.BlockAutomaticDoor
import me.arcanox.techmod.util.extensions.cardinals
import me.arcanox.techmod.util.reflect.HasTileEntityRenderer
import me.arcanox.techmod.util.reflect.ModTileEntity
import me.arcanox.techmod.util.toVec3d
import net.minecraft.client.renderer.texture.ITickable
import net.minecraft.entity.EntityType
import net.minecraft.nbt.CompoundNBT
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.state.properties.DoorHingeSide
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min

@ModTileEntity(Constants.Blocks.AutomaticDoor, BlockAutomaticDoor::class)
@HasTileEntityRenderer(AutomaticDoorTileRenderer::class)
class TileEntityAutomaticDoor(type: TileEntityType<in TileEntity>) : TileEntityBase(type), ITickable {
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
		val progress = max(min(fTicks / TicksToOpen.toFloat(), 1.0f), 0.0f);
		val eased = (cos(progress.toDouble() * Math.PI + Math.PI) + 1.0f).toFloat() / 2.0f;
		return 90.0f * eased;
	}
	
	override fun getRenderBoundingBox(): AxisAlignedBB = super.getRenderBoundingBox().expand(0.0, 1.0, 0.0)
	
	@OnlyIn(Dist.CLIENT)
	override fun hasFastRenderer(): Boolean = false
	
	override fun tick() {
		if (this.open && this.animTicks < TicksToOpen) this.animTicks++;
		if (!this.open && this.animTicks > 0) this.animTicks--;
		
		val world = this.world!!;
		
		if (world.isRemote) return;
		
		val canChangeState = this.animTicks == 0 || this.animTicks == TicksToOpen;
		
		if (canChangeState) {
			val state = world.getBlockState(pos);
			val middleDoor = this.pos.toVec3d().add(0.5, 1.0, 0.5);
			val hasDoorNeighbor = this.pos.cardinals().any { world.getBlockState(it).block === this.blockState };
			val checkPoint = when {
				hasDoorNeighbor -> {
					val facing = state.get(BlockStateProperties.FACING);
					val hinge = state.get(BlockStateProperties.DOOR_HINGE);
					val offsetDir = when (hinge) {
						DoorHingeSide.LEFT  -> facing.rotateYCCW()
						DoorHingeSide.RIGHT -> facing.rotateY()
						else                -> facing
					}
					
					middleDoor.add(offsetDir.xOffset * 0.5, 0.0, offsetDir.zOffset * 0.5);
				}
				else            -> middleDoor
			}
			val entities = world.getEntitiesWithinAABB(EntityType.PLAYER, this.checkAABB.offset(checkPoint), Predicates.alwaysTrue());
			val shouldBeOpen = entities.any();
			
			if (shouldBeOpen != this.open) {
				this.open = shouldBeOpen;
				world.setBlockState(this.pos, state.with(BlockStateProperties.OPEN, this.open));
				this.markDirty();
			}
		}
	}
	
	override fun getUpdateTag(): CompoundNBT = super.getUpdateTag().also {
		it.putBoolean("open", this.open)
	}
	
	override fun handleUpdateTag(tag: CompoundNBT) {
		super.handleUpdateTag(tag);
		
		this.readingNbt = true;
		this.open = tag.getBoolean("open");
		this.readingNbt = false;
	}
}