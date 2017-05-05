package me.arcanox.techmod.client.tileentities.renderers

import me.arcanox.techmod.TechMod
import me.arcanox.techmod.client.util.render.ModelHelper
import me.arcanox.techmod.common.tileentities.TileEntityAutomaticDoor
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object TESRAutomaticDoor : TileEntitySpecialRenderer<TileEntityAutomaticDoor>(), ITESRWithModels {
	var modelFrame: IBakedModel? = null;
	var modelGlass: IBakedModel? = null;
	
	override fun loadModels() {
		modelFrame = ModelHelper.bakeModel(ModelLoaderRegistry.getModel(ResourceLocation(TechMod.ModID, "block/automatic_door_frame")))
		modelGlass = ModelHelper.bakeModel(ModelLoaderRegistry.getModel(ResourceLocation(TechMod.ModID, "block/automatic_door_glass")))
	}
	
	override fun renderTileEntityAt(te: TileEntityAutomaticDoor?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
		if (modelFrame ?: modelGlass == null) return;
	}
}