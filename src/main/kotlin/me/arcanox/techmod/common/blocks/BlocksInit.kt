package me.arcanox.techmod.common.blocks

import me.arcanox.lib.common.blocks.BlockBase
import me.arcanox.techmod.TechModItemGroup
import me.arcanox.techmod.util.Logger
import me.arcanox.lib.util.reflect.HasBlockItem
import me.arcanox.lib.util.reflect.ModBlock
import me.arcanox.lib.util.reflect.ReflectionHelper
import me.arcanox.lib.util.reflect.classHasAnnotation
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.reflect.KClass


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object BlocksInit {
	internal val blocks = mutableMapOf<String, Block>()
	internal val blockClasses = mutableMapOf<KClass<out Block>, Block>()
	internal val blockItems = mutableMapOf<String, BlockItem>()
	
	// region Block Registration
	
	@SubscribeEvent
	fun registerBlocks(event: RegistryEvent.Register<Block>) {
		ReflectionHelper.forInstancesWithAnnotation(ModBlock::class, BlockBase::class) { block, _ ->
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
			val itemProperties = Item.Properties().group(TechModItemGroup).maxStackSize(annotation.maxStackSize);
			val blockItem = BlockItem(block, itemProperties).apply { registryName = block.registryName };
			
			this.blockItems += Pair(name, blockItem);
			
			return@map blockItem;
		};
		
		event.registry.registerAll(*blockItems.toTypedArray());
	}
	
	// endregion Block Registration
}