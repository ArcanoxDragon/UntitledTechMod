package me.arcanox.techmod.client.tileentities.renderers

import me.arcanox.techmod.TechMod
import me.arcanox.techmod.client.util.render.ModelHelper
import me.arcanox.techmod.util.reflect.hasAnnotation
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.common.model.IModelState
import org.lwjgl.opengl.GL11
import java.lang.reflect.Field

@Target(AnnotationTarget.FIELD)
annotation class ModelLocation(val location: String)

interface ITESRWithModels {
	fun loadModels(e: ModelBakeEvent);
}

abstract class TESRWithModels<T : TileEntity> : TileEntitySpecialRenderer<T>(), ITESRWithModels {
	val render: BlockModelRenderer by lazy { Minecraft.getMinecraft().blockRendererDispatcher.blockModelRenderer };
	val tessellator: Tessellator by lazy { Tessellator.getInstance() };
	
	abstract fun transformInCube(te: T, x: Double, y: Double, z: Double, partialTicks: Float, renderPass: Int)
	abstract fun renderModels(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, renderPass: Int, buffer: BufferBuilder)
	abstract val renderPasses: Int;
	
	protected open var requestedModelStates = emptyList<IModelState>();
	
	protected fun renderModel(world: IBlockAccess, model: IBakedModel, blockState: IBlockState, pos: BlockPos) {
		this.render.renderModel(world, model, blockState, pos, this.tessellator.buffer, false);
	}
	
	private fun forEachModelField(singleModelAction: (Field) -> Unit, multiModelAction: (Field) -> Unit) {
		val modelType = IBakedModel::class.java;
		val modelMapType = Map::class.java;
		
		// Load all single-state models
		this.javaClass
			.declaredFields
			.filter { modelType.isAssignableFrom(it.type) }
			.filter { it.hasAnnotation<ModelLocation>() }
			.forEach(singleModelAction);
		
		// Load all multi-state model maps
		this.javaClass
			.declaredFields
			.filter { it.type.typeParameters.size == 2 }
			.filter { modelMapType.isAssignableFrom(it.type) }
			.filter { it.hasAnnotation<ModelLocation>() }
			.forEach(multiModelAction)
	}
	
	final override fun loadModels(e: ModelBakeEvent) {
		this.forEachModelField(
			{
				val annotation = it.getAnnotation(ModelLocation::class.java);
				val bakedModel = ModelHelper.getBakedModel(ResourceLocation(TechMod.ModID, annotation.location));
				
				it.isAccessible = true;
				it.set(this, bakedModel);
			}, {
				val annotation = it.getAnnotation(ModelLocation::class.java);
				val bakedModels = ModelHelper.getBakedModelForStates(ResourceLocation(TechMod.ModID, annotation.location), this.requestedModelStates)
				
				it.isAccessible = true;
				it.set(this, bakedModels);
			});
	}
	
	override fun render(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, unknown1: Float) {
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.color(1f, 1f, 1f);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z); // account for player offset in TESR dispatcher
		
		for (pass in 0 until this.renderPasses) {
			GlStateManager.pushMatrix();
			
			this.transformInCube(te, x, y, z, partialTicks, pass); // all transforms done in here will originate from the x-,y-,z- corner of this block
			
			GlStateManager.translate(-te.pos.x.toDouble(), -te.pos.y.toDouble(), -te.pos.z.toDouble()) // account for blockpos offset in TESR dispatcher
			
			// begin actual render
			this.tessellator.buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			
			this.renderModels(te, x, y, z, partialTicks, destroyStage, pass, this.tessellator.buffer);
			
			this.tessellator.draw();
			// end actual render
			
			GlStateManager.popMatrix();
		}
		
		GlStateManager.popMatrix();
	}
}