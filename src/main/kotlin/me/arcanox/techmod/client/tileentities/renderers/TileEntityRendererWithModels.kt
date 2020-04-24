package me.arcanox.techmod.client.tileentities.renderers

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation


abstract class TileEntityRendererWithModels<T : TileEntity>(rendererDispatcherIn: TileEntityRendererDispatcher) : TileEntityRenderer<T>(rendererDispatcherIn) {
	protected fun lazyModel(rl: ResourceLocation) = lazy { Minecraft.getInstance().modelManager.getModel(rl) }
}