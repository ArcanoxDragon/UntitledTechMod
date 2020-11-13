package me.arcanox.techmod.common.tileentities

import com.google.common.base.Predicates
import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.client.tileentities.renderers.AutomaticDoorTileRenderer
import me.arcanox.techmod.common.blocks.AutomaticDoorBlock
import me.arcanox.techmod.util.extensions.horizontalNeighbors
import me.arcanox.techmod.util.reflect.HasTileEntityRenderer
import me.arcanox.techmod.util.reflect.ModTileEntity
import me.arcanox.techmod.util.toVector3d
import net.minecraft.block.BlockState
import net.minecraft.entity.EntityType
import net.minecraft.nbt.CompoundNBT
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.state.properties.DoorHingeSide
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.util.math.AxisAlignedBB
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min

@ModTileEntity(Constants.Blocks.AutomaticDoor, AutomaticDoorBlock::class)
@HasTileEntityRenderer(AutomaticDoorTileRenderer::class)
class AutomaticDoorTileEntity : TileEntityBase(TileEntitiesInit.getTileEntityType<AutomaticDoorTileEntity>()), ITickableTileEntity {
	companion object {
		const val OpenKey = "open";
		
		const val TicksToOpen = 15;
		const val Range = 4.0; // blocks // TODO: make this configurable later
		const val Height = 2.0; // blocks
	}
	
	private var firstTick = true;
	private var animTicks = 0;
	private var internalOpen = false;
	private val checkAABB = Range.let { r -> AxisAlignedBB(-r, 0.0, -r, r, Height, r) };
	
	var open: Boolean
		get() = this.internalOpen
		set(value) {
			if (this.animTicks == 0 || this.animTicks == TicksToOpen) this.internalOpen = value;
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
	
	override fun tick() {
		if (firstTick) {
			// Door shouldn't animate open on the client if it's been loaded as open from the server right after joining the world
			
			this.animTicks = if (this.open) TicksToOpen else 0;
			this.firstTick = false;
		}
		
		// Increment or decrement animation progress depending on desired door state
		if (this.open && this.animTicks < TicksToOpen) this.animTicks++;
		if (!this.open && this.animTicks > 0) this.animTicks--;
		
		val world = this.world!!;
		
		if (world.isRemote) return;
		
		val canChangeState = this.animTicks == 0 || this.animTicks == TicksToOpen;
		
		if (canChangeState) {
			val state = world.getBlockState(pos);
			val middleDoor = this.pos.toVector3d().add(0.5, 0.0, 0.5);
			val hasDoorNeighbor = this.pos.horizontalNeighbors().any { world.getBlockState(it).block === this.blockState };
			
			// Find the center point of the check area. This will be the middle of the door on a single door,
			// or the exact center between two doors for a double door. TODO: Allow one-way doors?
			val checkPoint = when {
				hasDoorNeighbor -> {
					val facing = state.get(BlockStateProperties.FACING);
					val hinge = state.get(BlockStateProperties.DOOR_HINGE);
					val offsetDir = when (hinge) {
						DoorHingeSide.LEFT -> facing.rotateYCCW()
						DoorHingeSide.RIGHT -> facing.rotateY()
						else                -> facing
					}
					
					middleDoor.add(offsetDir.xOffset * 0.5, 0.0, offsetDir.zOffset * 0.5);
				}
				else            -> middleDoor
			}
			
			// See if there are any entities within the check area. TODO: Allow filtering later on?
			val entities = world.getEntitiesWithinAABB(EntityType.PLAYER, this.checkAABB.offset(checkPoint), Predicates.alwaysTrue());
			val shouldBeOpen = entities.any();
			
			if (shouldBeOpen != this.open) {
				// Change the door state
				
				this.open = shouldBeOpen;
				world.setBlockState(this.pos, state.with(BlockStateProperties.OPEN, this.open));
				this.markDirty();
			}
		}
	}
	
	override fun write(compound: CompoundNBT): CompoundNBT = super.write(compound).also {
		it.putBoolean(OpenKey, this.open);
	}
	
	override fun read(blockState: BlockState, compound: CompoundNBT) {
		super.read(blockState, compound)
		
		this.internalOpen = compound.getBoolean(OpenKey);
	}
	
	override fun getUpdateTag(): CompoundNBT = super.getUpdateTag().also {
		it.putBoolean(OpenKey, this.open);
	}
	
	override fun handleUpdateTag(blockState: BlockState, tag: CompoundNBT) {
		super.handleUpdateTag(blockState, tag);
		
		this.internalOpen = tag.getBoolean(OpenKey);
	}
}