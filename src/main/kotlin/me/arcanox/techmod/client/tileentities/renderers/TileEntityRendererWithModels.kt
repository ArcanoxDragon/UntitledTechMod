package me.arcanox.techmod.client.tileentities.renderers

import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@Target(AnnotationTarget.CLASS)
annotation class LoadsModels();

interface ILoadsModels {
	fun getModelLocations(): Sequence<ResourceLocation>;
}

@OnlyIn(Dist.CLIENT)
abstract class TileEntityRendererWithModels<T : TileEntity>(
	rendererDispatcher: TileEntityRendererDispatcher
) : TileEntityRenderer<T>(rendererDispatcher), ILoadsModels {}