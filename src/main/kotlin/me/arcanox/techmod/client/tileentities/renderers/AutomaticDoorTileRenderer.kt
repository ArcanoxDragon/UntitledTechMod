package me.arcanox.techmod.client.tileentities.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import me.arcanox.techmod.TechMod
import me.arcanox.techmod.client.util.render.ConsumesModels
import me.arcanox.techmod.client.util.render.IModelConsumer
import me.arcanox.techmod.client.util.render.ModelHelper
import me.arcanox.techmod.common.tileentities.AutomaticDoorTileEntity
import me.arcanox.techmod.util.extensions.translateVoxels
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Quaternion
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.Vector3f
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.state.properties.DoorHingeSide
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
@ConsumesModels
class AutomaticDoorTileRenderer(rendererDispatcher: TileEntityRendererDispatcher) : TileEntityRendererWithModels<AutomaticDoorTileEntity>(rendererDispatcher) {
	companion object : IModelConsumer {
		private val DoorModelLocation = ResourceLocation(TechMod.ModID, "block/automatic_door/door");
		
		val DoorModel = ModelHelper.getModel(DoorModelLocation)
		
		override fun reloadModels() {
			DoorModel.invalidate();
		}
		
		override fun getModelLocations() = sequence {
			yield(DoorModelLocation);
		}
	}
	
	override fun render(tileEntity: AutomaticDoorTileEntity, partialTicks: Float, matrixStack: MatrixStack, buffer: IRenderTypeBuffer, combinedLight: Int, combinedOverlay: Int) {
		val world = tileEntity.world ?: return;
		val state = world.getBlockState(tileEntity.pos);
		val hinge = state.get(BlockStateProperties.DOOR_HINGE);
		val facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
		val builder = buffer.getBuffer(RenderType.getCutout());
		
		// Transform based on door facing/animation
		matrixStack.push();
		
		matrixStack.translateVoxels(8, 0, 8); // Center of block
		matrixStack.rotate(Quaternion(Vector3f.YN, facing.horizontalAngle, true));
		matrixStack.translateVoxels(-8, 0, -8); // Back to origin
		
		when (hinge) {
			DoorHingeSide.LEFT -> {
				// Hinge is at 1,0,1, rotate counter-clockwise
				matrixStack.translateVoxels(15, 0, 1);
				matrixStack.rotate(Quaternion(Vector3f.YP, tileEntity.getRotation(partialTicks), true));
				matrixStack.translateVoxels(-15, 0, -1);
			}
			else               -> {
				// Hinge is at 1,0,1, rotate clockwise
				matrixStack.translateVoxels(1, 0, 1);
				matrixStack.rotate(Quaternion(Vector3f.YP, -tileEntity.getRotation(partialTicks), true));
				matrixStack.translateVoxels(-1, 0, -1);
			}
		}
		
		// Render the door
		this.renderModel(DoorModel, builder, matrixStack, state, world.random, combinedLight, combinedOverlay);
		
		matrixStack.pop();
	}
	
}