package me.arcanox.techmod.client.tileentities.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import me.arcanox.techmod.TechMod
import me.arcanox.techmod.client.util.render.ConsumesModels
import me.arcanox.techmod.client.util.render.IModelConsumer
import me.arcanox.techmod.common.tileentities.AutomaticDoorTileEntity
import me.arcanox.techmod.util.Directions
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.model.ModelRotation
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.model.data.EmptyModelData

@OnlyIn(Dist.CLIENT)
@ConsumesModels
class AutomaticDoorTileRenderer(rendererDispatcher: TileEntityRendererDispatcher) : TileEntityRendererWithModels<AutomaticDoorTileEntity>(rendererDispatcher) {
	companion object : IModelConsumer {
		private const val RenderPassDoor = 0;
		private const val RenderPassMount = 1;
		
		private val MountModelLocation = ResourceLocation(TechMod.ModID, "block/automatic_door/mount");
		private val DoorModelLocation = ResourceLocation(TechMod.ModID, "block/automatic_door/door");
		
		override fun getModelLocations() = sequence {
			yield(MountModelLocation);
			yield(DoorModelLocation);
		}
	}
	
	private val mountModel = lazyModel(MountModelLocation);
	private val doorModel = lazyModel(DoorModelLocation);
	
	override fun render(tileEntity: AutomaticDoorTileEntity, partialTicks: Float, matrixStack: MatrixStack, buffer: IRenderTypeBuffer, combinedLight: Int, combinedOverlay: Int) {
		val world = tileEntity.world ?: return;
		val state = world.getBlockState(tileEntity.pos);
		val facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
		val rotation = ModelRotation.getModelRotation(0, facing.rotateYCCW().horizontalAngle.toInt());
		val matrix = matrixStack.last;
		
		// Render the door
		val builder = buffer.getBuffer(RenderType.getCutout());
		
		this.doorModel.value.getQuads(state, null, world.random, EmptyModelData.INSTANCE).forEach {
			builder.addVertexData(matrix, it, 1f, 1f, 1f, combinedLight, combinedOverlay, false);
		}
	}
	
}