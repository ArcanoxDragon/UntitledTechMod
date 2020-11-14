package me.arcanox.techmod.client.tiles.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import me.arcanox.lib.client.util.extensions.renderSimple
import me.arcanox.lib.client.util.render.ConsumesModels
import me.arcanox.lib.client.util.render.ModelLocation
import me.arcanox.lib.util.LazyCache
import me.arcanox.lib.util.extensions.pushAnd
import me.arcanox.lib.util.extensions.translateVoxels
import me.arcanox.techmod.common.tiles.AutomaticDoorTileEntity
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.state.properties.DoorHingeSide
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
@ConsumesModels
class AutomaticDoorTileRenderer(rendererDispatcher: TileEntityRendererDispatcher) : TileEntityRenderer<AutomaticDoorTileEntity>(rendererDispatcher) {
	companion object {
		@ModelLocation("block/automatic_door/door")
		lateinit var DoorModel: LazyCache<IBakedModel>;
	}
	
	override fun render(tileEntity: AutomaticDoorTileEntity, partialTicks: Float, matrixStack: MatrixStack, buffer: IRenderTypeBuffer, combinedLight: Int, combinedOverlay: Int) {
		val world = tileEntity.world ?: return;
		val state = world.getBlockState(tileEntity.pos);
		val hinge = state.get(BlockStateProperties.DOOR_HINGE);
		val facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
		val vertexBuilder = buffer.getBuffer(RenderType.getCutout());
		
		matrixStack.pushAnd {
			// Transform to the rotation point based on door facing/animation
			translateVoxels(8, 0, 8); // Center of block
			rotate(Quaternion(Vector3f.YN, facing.horizontalAngle, true));
			translateVoxels(-8, 0, -8); // Back to origin
			
			// Rotate based on the animation state of the door
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
			DoorModel.renderSimple(vertexBuilder, matrixStack, state, world.random, combinedLight, combinedOverlay);
		}
	}
	
}