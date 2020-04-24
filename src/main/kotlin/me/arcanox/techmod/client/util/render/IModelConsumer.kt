package me.arcanox.techmod.client.util.render

import net.minecraft.util.ResourceLocation

@Target(AnnotationTarget.CLASS)
annotation class ConsumesModels;

public interface IModelConsumer {
	fun getModelLocations(): Sequence<ResourceLocation>;
}