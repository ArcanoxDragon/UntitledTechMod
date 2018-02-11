package me.arcanox.techmod.client.tileentities.renderers

import me.arcanox.techmod.common.tileentities.TileEntityAutomaticDoor
import net.minecraft.block.BlockDoor
import net.minecraft.block.BlockHorizontal
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object TESRAutomaticDoor : TESRWithModels<TileEntityAutomaticDoor>() {
	private const val RenderPassDoor = 0;
	private const val RenderPassMount = 1;
	
	@ModelLocation("block/automatic_door/mount")
	var mapMount: Map<IModelState, IBakedModel>? = null;
	@ModelLocation("block/automatic_door/door")
	var mapDoor: Map<IModelState, IBakedModel>? = null;
	
	override val renderPasses: Int = 21
	
	override var requestedModelStates: List<IModelState> = listOf(ModelRotation.X0_Y0,
	                                                              ModelRotation.X0_Y90,
	                                                              ModelRotation.X0_Y180,
	                                                              ModelRotation.X0_Y270)
	
	override fun transformInCube(te: TileEntityAutomaticDoor, x: Double, y: Double, z: Double, partialTicks: Float, renderPass: Int) {
		if (!te.hasWorld()) return;
		
		when (renderPass) {
			RenderPassDoor -> transformDoor(te, partialTicks)
		}
	}
	
	override fun renderModels(te: TileEntityAutomaticDoor, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, renderPass: Int, buffer: BufferBuilder) {
		if (!te.hasWorld()) return;
		
		val world = MinecraftForgeClient.getRegionRenderCache(te.world, te.pos);
		val state = world.getBlockState(te.pos);
		val half = state.getValue(BlockDoor.HALF);
		val facing = state.getValue(BlockHorizontal.FACING);
		val modelRot = ModelRotation.getModelRotation(0, facing.rotateYCCW().horizontalAngle.toInt());
		if (half == BlockDoor.EnumDoorHalf.UPPER) return;
		
		val model = when (renderPass) {
			RenderPassDoor  -> this.mapDoor?.get(modelRot)
			RenderPassMount -> this.mapMount?.get(modelRot)
			else            -> null
		}
		
		if (model == null) return;
		
		this.render.renderModel(world, model, world.getBlockState(te.pos), te.pos, buffer, false);
	}
	
	// region Transform
	
	private fun transformDoor(te: TileEntityAutomaticDoor, partialTicks: Float) {
		val state = te.world.getBlockState(te.pos);
		val hinge = state.getValue(BlockDoor.HINGE);
		val facing = state.getValue(BlockHorizontal.FACING);
		
		GlStateManager.translate(8.0 / 16.0, 0.0, 8.0 / 16.0); // center y axis of block is at voxel 8,0,8
		GlStateManager.rotate(facing.rotateYCCW().horizontalAngle, 0.0f, -1.0f, 0.0f); // rotate so model origin is at correct rotation point
		GlStateManager.translate(-8.0 / 16.0, 0.0, -8.0 / 16.0); // center y axis of block is at voxel 8,0,8
		
		when (hinge) {
			BlockDoor.EnumHingePosition.LEFT -> {
				GlStateManager.translate(1.0 / 16.0, 0.0, 1.0 / 16.0); // hinge is at voxel 1,0,1
				GlStateManager.rotate(te.getRotation(partialTicks), 0.0f, 1.0f, 0.0f);
				GlStateManager.translate(-1.0 / 16.0, 0.0, -1.0 / 16.0);
			}
			else                             -> {
				GlStateManager.translate(1.0 / 16.0, 0.0, 15.0 / 16.0); // hinge is at voxel 15,0,1
				GlStateManager.rotate(-te.getRotation(partialTicks), 0.0f, 1.0f, 0.0f);
				GlStateManager.translate(-1.0 / 16.0, 0.0, -15.0 / 16.0);
			}
		}
		
		GlStateManager.translate(8.0 / 16.0, 0.0, 8.0 / 16.0); // center y axis of block is at voxel 8,0,8
		GlStateManager.rotate(facing.rotateYCCW().horizontalAngle, 0.0f, 1.0f, 0.0f); // rotate back to original orientation for this facing
		GlStateManager.translate(-8.0 / 16.0, 0.0, -8.0 / 16.0); // center y axis of block is at voxel 8,0,8
	}
	
	// endregion
}