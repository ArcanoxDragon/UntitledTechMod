package me.arcanox.techmod.client.util.render

import me.arcanox.techmod.util.Logger
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelBlock
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.model.ITransformation
import net.minecraftforge.common.model.TRSRTransformation


object ModelHelper {
	fun bakeModel(model: IModel, transform: ITransformation = TRSRTransformation.identity(), lockUvs: Boolean = true): IBakedModel? {
		val bakeModel = ModelBakery::class.java.getDeclaredMethod("bakeModel", ModelBlock::class.java, ITransformation::class.java, Boolean::class.java)
		val vanillaLoaderClass = ModelLoader::class.nestedClasses.find { it.simpleName.equals("VanillaLoader") };
		val vanillaLoaderField = vanillaLoaderClass?.java?.getDeclaredField("INSTANCE");
		
		vanillaLoaderField?.isAccessible = true;
		
		val vanillaLoader = vanillaLoaderField?.get(null);
		
		if (vanillaLoader == null) {
			Logger.warn("Could not find instance of VanillaLoder to bake models!");
			return null;
		}
		
		val modelLoaderField = vanillaLoader.javaClass.getDeclaredField("loader");
		
		modelLoaderField?.isAccessible = true;
		
		val modelLoader = modelLoaderField?.get(vanillaLoader);
		
		if (modelLoader == null) {
			Logger.warn("Could not find instance of ModelLoader to bake models!");
			return null;
		}
		
		bakeModel.isAccessible = true;
		
		if (!model.javaClass.name.endsWith("ModelLoader\$VanillaModelWrapper")) {
			throw IllegalArgumentException("Model must be a VanillaModelWrapper, not a ${model.javaClass.name}");
		}
		
		val modelField = model.javaClass.getDeclaredField("model");
		
		modelField.isAccessible = true;
		
		val modelToBake = modelField.get(model) as ModelBlock;
		// TODO: Figure out why this is trying to load particle sprite, also figure out why it can't
		return bakeModel.invoke(modelLoader, modelToBake, transform, lockUvs) as IBakedModel;
	}
	
	
}