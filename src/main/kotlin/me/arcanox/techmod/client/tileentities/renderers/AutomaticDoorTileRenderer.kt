package me.arcanox.techmod.client.tileentities.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import me.arcanox.techmod.common.tileentities.TileEntityAutomaticDoor
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class AutomaticDoorTileRenderer(rendererDispatcher: TileEntityRendererDispatcher) : TileEntityRendererWithModels<TileEntityAutomaticDoor>(rendererDispatcher) {
	companion object {
		private const val RenderPassDoor = 0;
		private const val RenderPassMount = 1;
		
		private val MountModelLocation = ResourceLocation("block/automatic_door/mount");
		private val DoorModelLocation = ResourceLocation("block/automatic_door/door");
	}
	
	override fun render(tileEntityIn: TileEntityAutomaticDoor, partialTicks: Float, matrixStackIn: MatrixStack, bufferIn: IRenderTypeBuffer, combinedLightIn: Int, combinedOverlayIn: Int) {
	
	}
	
	override fun getModelLocations() = sequence {
		yield(MountModelLocation);
		yield(DoorModelLocation);
	}
	
}