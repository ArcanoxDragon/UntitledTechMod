package me.arcanox.techmod.api.blocks

import me.arcanox.techmod.common.blocks.BlockAutomaticDoor
import me.arcanox.techmod.common.blocks.base.BlockBase
import me.arcanox.techmod.util.IInitStageHandler
import me.arcanox.techmod.util.Logger

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object BlocksAPI : IBlockAPI, IInitStageHandler {
	val blocks: MutableMap<String, Block> = mutableMapOf()
	val blockItems: MutableMap<String, ItemBlock> = mutableMapOf()
	
	init {
		arrayOf(
				::BlockAutomaticDoor
		).forEach { new ->
			val block = new();
			this.blocks += Pair(block.apiName, block);
		}
	}
	
	@SubscribeEvent
	fun registerBlocks(event: RegistryEvent.Register<Block>): Unit {
		Logger.info("Registering blocks...");
		
		this.blocks.values.forEach(event.registry::register);
	}
	
	@SubscribeEvent
	fun registerBlockItems(event: RegistryEvent.Register<Item>): Unit {
		Logger.info("Registering block items...");
		
		for ((name, block) in this.blocks) {
			// Only block classes with @HasItemBlock directly applied to them will automatically receive ItemBlocks
			if (block.javaClass.getDeclaredAnnotation(HasItemBlock::class.java) != null) {
				val blockItem = ItemBlock(block)
				
				blockItem.registryName = block.registryName;
				
				this.blockItems += Pair(name, blockItem);
				event.registry.register(blockItem);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	fun registerBlockModels(): Unit {
		for ((name, block) in this.blocks) {
		
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
