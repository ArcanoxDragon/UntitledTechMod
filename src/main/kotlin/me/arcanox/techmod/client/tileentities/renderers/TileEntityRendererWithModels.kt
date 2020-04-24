package me.arcanox.techmod.client.tileentities.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import me.arcanox.techmod.util.LazyCache
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.client.model.data.EmptyModelData
import java.util.*

abstract class TileEntityRendererWithModels<T : TileEntity>(rendererDispatcherIn: TileEntityRendererDispatcher) : TileEntityRenderer<T>(rendererDispatcherIn) {
	protected fun renderModel(model: IBakedModel, vertexBuilder: IVertexBuilder, matrixStack: MatrixStack, blockState: BlockState, random: Random, combinedLight: Int, combinedOverlay: Int) {
		val matrix = matrixStack.last;
		
		model.getQuads(blockState, null, random, EmptyModelData.INSTANCE).forEach {
			vertexBuilder.addVertexData(matrix, it, 1f, 1f, 1f, combinedLight, combinedOverlay, false);
		}
	}
	
	protected fun renderModel(model: LazyCache<IBakedModel>,
	                          vertexBuilder: IVertexBuilder,
	                          matrixStack: MatrixStack,
	                          blockState: BlockState,
	                          random: Random,
	                          combinedLight: Int,
	                          combinedOverlay: Int) {
		this.renderModel(model.value, vertexBuilder, matrixStack, blockState, random, combinedLight, combinedOverlay);
	}
}