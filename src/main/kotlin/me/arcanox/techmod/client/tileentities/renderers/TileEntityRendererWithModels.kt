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
	/**
	 * Renders the provided IBakedModel using the provided parameters
	 */
	protected fun renderModel(model: IBakedModel, vertexBuilder: IVertexBuilder, matrixStack: MatrixStack, blockState: BlockState, random: Random, combinedLight: Int, combinedOverlay: Int) {
		val matrix = matrixStack.last;
		
		// Simply render a model with standard lighting and colors by iterating through its quads and adding them to the vertex builder
		model.getQuads(blockState, null, random, EmptyModelData.INSTANCE).forEach {
			vertexBuilder.addVertexData(matrix, it, 1f, 1f, 1f, combinedLight, combinedOverlay, false);
		}
	}
	
	/**
	 * A wrapper for renderModel(IBakedModel, ...) that accepts a LazyCache<IBakedModel> instead, for readability purposes in a TileEntityRendererWithModels<T>
	 */
	protected fun renderModel(model: LazyCache<IBakedModel>, vertexBuilder: IVertexBuilder, matrixStack: MatrixStack, blockState: BlockState, random: Random, combinedLight: Int, combinedOverlay: Int) {
		this.renderModel(model.value, vertexBuilder, matrixStack, blockState, random, combinedLight, combinedOverlay);
	}
}