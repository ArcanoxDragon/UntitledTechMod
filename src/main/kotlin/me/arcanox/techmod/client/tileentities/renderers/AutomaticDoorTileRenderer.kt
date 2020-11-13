package me.arcanox.techmod.client.tileentities.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import me.arcanox.techmod.TechMod
import me.arcanox.techmod.client.util.render.ConsumesModels
import me.arcanox.techmod.client.util.render.IModelConsumer
import me.arcanox.techmod.client.util.render.ModelLoader
import me.arcanox.techmod.common.tileentities.AutomaticDoorTileEntity
import me.arcanox.techmod.util.extensions.pushAnd
import me.arcanox.techmod.util.extensions.translateVoxels
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.state.properties.DoorHingeSide
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
@ConsumesModels
class AutomaticDoorTileRenderer(rendererDispatcher: TileEntityRendererDispatcher) : TileEntityRendererWithModels<AutomaticDoorTileEntity>(rendererDispatcher) {
	companion object : IModelConsumer {
		private val DoorModelLocation = ResourceLocation(TechMod.ModID, "block/automatic_door/door");
		
		val DoorModel = ModelLoader.getModel(DoorModelLocation)
		
		override fun reloadModels() {
			DoorModel.invalidate();
			DoorModel.poke(); // Load it now
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
		
		matrixStack.pushAnd {
			// Transform based on door facing/animation
			translateVoxels(8, 0, 8); // Center of block
			rotate(Quaternion(Vector3f.YN, facing.horizontalAngle, true));
			translateVoxels(-8, 0, -8); // Back to origin
			
			when (hinge) {
				DoorHingeSide.LEFT -> {
					// Hinge is at voxel 15,0,1, rotate counter-clockwise
					translateVoxels(15, 0, 1);
					rotate(Quaternion(Vector3f.YP, tileEntity.getRotation(partialTicks), true));
					translateVoxels(-15, 0, -1);
				}
				else               -> {
					// Hinge is at voxel 1,0,1, rotate clockwise
					translateVoxels(1, 0, 1);
					rotate(Quaternion(Vector3f.YP, -tileEntity.getRotation(partialTicks), true));
					translateVoxels(-1, 0, -1);
				}
			}
			
			// Render the door
			this@AutomaticDoorTileRenderer.renderModel(DoorModel, builder, matrixStack, state, world.random, combinedLight, combinedOverlay);
		}
	}
	
}