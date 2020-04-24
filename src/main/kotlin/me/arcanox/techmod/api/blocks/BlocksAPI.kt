package me.arcanox.techmod.api.blocks

import me.arcanox.techmod.ItemGroup
import me.arcanox.techmod.common.blocks.BlockBase
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.HasBlockItem
import me.arcanox.techmod.util.reflect.ModBlock
import me.arcanox.techmod.util.reflect.ReflectionHelper
import me.arcanox.techmod.util.reflect.classHasAnnotation
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.reflect.KClass

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object BlocksAPI : IBlockAPI {
	val blocks = mutableMapOf<String, Block>()
	val blockClasses = mutableMapOf<KClass<out Block>, Block>()
	val blockItems = mutableMapOf<String, BlockItem>()
	
	// region Registration
	
	@SubscribeEvent
	fun registerBlocks(event: RegistryEvent.Register<Block>) {
		ReflectionHelper
			.getInstancesWithAnnotation(ModBlock::class, BlockBase::class)
			.forEach { (block, _) ->
				this.blocks += Pair(block.apiName, block);
				this.blockClasses += Pair(block.javaClass.kotlin, block);
			};
		
		Logger.info("Registering ${this.blocks.size} blocks...");
		
		this.blocks.values.forEach { event.registry.register(it) };
	}
	
	@SubscribeEvent
	fun registerBlockItems(event: RegistryEvent.Register<Item>) {
		val blocksWithItems = this.blocks.filterValues { it.classHasAnnotation<HasBlockItem>() };
		
		Logger.info("Registering ${blocksWithItems.size} block items...");
		
		// Only block classes with @HasBlockItem directly applied to them will automatically receive BlockItems
		val blockItems = blocksWithItems.map { (name, block) ->
			val annotation = block.javaClass.getDeclaredAnnotation(HasBlockItem::class.java);
			val blockItem = BlockItem(
				block,
				Item.Properties().group(ItemGroup).maxStackSize(annotation.maxStackSize)
			).apply { registryName = block.registryName; };
			
			this.blockItems += Pair(name, blockItem);
			
			return@map blockItem;
		};
		
		event.registry.registerAll(*blockItems.toTypedArray());
	}
	
	// endregion Registration
	
	// region IBlockAPI contract
	
	override fun getBlock(name: String): Block? {
		if (name !in this.blocks) return null;
		
		return this.blocks[name];
	}
	
	override fun getBlockItem(name: String): BlockItem? {
		if (name !in this.blockItems) return null;
		
		return this.blockItems[name];
	}
	
	override fun getBlockItemStack(name: String, count: Int): ItemStack? {
		if (name !in this.blocks) return null;
		
		return ItemStack(this.getBlock(name)!!, count);
	}
	
	// endregion IBlockAPI contract
	
	internal fun getBlock(clazz: KClass<out Block>): Block? {
		if (clazz !in this.blockClasses) return null;
		
		return this.blockClasses[clazz];
	}
}
