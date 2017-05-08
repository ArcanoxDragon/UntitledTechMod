package me.arcanox.techmod.client.util.render

import me.arcanox.techmod.util.Logger
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelBlock
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.model.ITransformation
import net.minecraftforge.common.model.TRSRTransformation


object ModelHelper {
	fun getBakedModel(location: ResourceLocation, vertexFormat: VertexFormat = DefaultVertexFormats.BLOCK): IBakedModel {
		val model: IModel = ModelLoaderRegistry.getModelOrMissing(location);
		
		return model.bake(model.defaultState, vertexFormat, ModelLoader.defaultTextureGetter());
	}
	
	fun getBakedModelForStates(location: ResourceLocation, states: List<IModelState>): Map<IModelState, IBakedModel> {
		val model: IModel = ModelLoaderRegistry.getModelOrMissing(location);
		
		return states.associate {
			val baked = model.bake(it, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
			Pair(it, baked)
		}
	}
}