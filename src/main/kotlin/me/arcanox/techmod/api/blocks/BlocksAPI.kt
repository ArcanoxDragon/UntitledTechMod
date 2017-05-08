package me.arcanox.techmod.api.blocks

import me.arcanox.techmod.common.blocks.base.BlockBase
import me.arcanox.techmod.common.proxy.IClientInitHandler
import me.arcanox.techmod.common.proxy.IInitStageHandler
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.*
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@InitHandler
@ClientInitHandler
object BlocksAPI : IBlockAPI, IInitStageHandler, IClientInitHandler {
	val blocks: MutableMap<String, Block> = mutableMapOf()
	val blockItems: MutableMap<String, ItemBlock> = mutableMapOf()
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		val discoveredBlocks = ReflectionHelper
			.getInstancesWithAnnotation(e.asmData, ModBlock::class, BlockBase::class)
			.map { (block, _) -> Pair(block.apiName, block) };
		
		this.blocks += discoveredBlocks;
		
		Logger.info("Registering ${this.blocks.size} blocks...");
		
		this.registerBlocks();
		this.registerBlockItems();
	}
	
	override fun onClientPreInit(e: FMLPreInitializationEvent) {
		this.registerBlockItemModels();
	}
	
	fun registerBlocks(): Unit {
		this.blocks.values.forEach { GameRegistry.register(it) };
	}
	
	fun registerBlockItems(): Unit {
		val blocksWithItems = this.blocks.filterValues { it.classHasAnnotation<HasItemBlock>() };
		
		Logger.info("Registering ${blocksWithItems.size} block items...");
		
		// Only block classes with @HasItemBlock directly applied to them will automatically receive ItemBlocks
		for ((name, block) in blocksWithItems) {
			val blockItem = ItemBlock(block)
			
			blockItem.registryName = block.registryName;
			
			this.blockItems += Pair(name, blockItem);
			GameRegistry.register(blockItem);
		}
	}
	
	@SideOnly(Side.CLIENT)
	fun registerBlockItemModels(): Unit {
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
		
		return ItemStack(this.getBlock(name), count);
	}
}
