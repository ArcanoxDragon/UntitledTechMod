package me.arcanox.techmod.api.blocks

import me.arcanox.techmod.common.blocks.base.BlockBase
import me.arcanox.techmod.common.proxy.IClientInitHandler
import me.arcanox.techmod.common.proxy.IInitStageHandler
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.*
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@InitHandler
@ClientInitHandler
object BlocksAPI : IBlockAPI, IInitStageHandler, IClientInitHandler {
	val blocks: MutableMap<String, Block> = mutableMapOf()
	val blockItems: MutableMap<String, ItemBlock> = mutableMapOf()
	
	init {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		val discoveredBlocks = ReflectionHelper
			.getInstancesWithAnnotation(e.asmData, ModBlock::class, BlockBase::class)
			.map { (block, _) -> Pair(block.apiName, block) };
		
		this.blocks += discoveredBlocks;
	}
	
	@SubscribeEvent
	fun registerBlocks(e: RegistryEvent.Register<Block>) {
		Logger.info("Registering ${this.blocks.size} blocks...");
		
		this.blocks.values.forEach { e.registry.register(it) };
	}
	
	@SubscribeEvent
	fun registerBlockItems(e: RegistryEvent.Register<Item>) {
		val blocksWithItems = this.blocks.filterValues { it.classHasAnnotation<HasItemBlock>() };
		
		Logger.info("Registering ${blocksWithItems.size} block items...");
		
		// Only block classes with @HasItemBlock directly applied to them will automatically receive ItemBlocks
		for ((name, block) in blocksWithItems) {
			val blockItem = ItemBlock(block)
			
			blockItem.registryName = block.registryName;
			
			this.blockItems += Pair(name, blockItem);
			e.registry.register(blockItem);
		}
		
		if ( FMLCommonHandler.instance().side == Side.CLIENT )
			this.registerBlockItemModels();
	}
	
	@SideOnly(Side.CLIENT)
	fun registerBlockItemModels() {
		val blockItemsWithModel = this.blocks
			.filter { this.blockItems.containsKey(it.key) }
			.filter { it.value.classHasAnnotation<HasItemModel>() };
		
		Logger.info("Registering models for ${blockItemsWithModel.size} block items...");
		
		blockItemsWithModel.forEach { (name, block) ->
			val blockItem = this.blockItems[name]!!;
			ModelLoader.setCustomModelResourceLocation(blockItem, 0, ModelResourceLocation(block.registryName.toString()));
		}
	}
	
	override fun getBlock(name: String): Block? {
		if (name !in this.blocks) return null;
		
		return this.blocks[name];
	}
	
	override fun getBlockItem(name: String): ItemBlock? {
		if (name !in this.blockItems) return null;
		
		return this.blockItems[name];
	}
	
	override fun getBlockItemStack(name: String, count: Int): ItemStack? {
		if (name !in this.blocks) return null;
		
		return ItemStack(this.getBlock(name)!!, count);
	}
}
