package me.arcanox.techmod.common.blocks

import me.arcanox.lib.common.blocks.BlockBase
import me.arcanox.techmod.TechModItemGroup
import me.arcanox.techmod.util.Logger
import me.arcanox.lib.util.reflect.HasBlockItem
import me.arcanox.lib.util.reflect.ModBlock
import me.arcanox.lib.util.reflect.ReflectionHelper
import me.arcanox.lib.util.reflect.classHasAnnotation
import me.arcanox.techmod.TechMod
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.reflect.KClass

object Blocks {
	internal val blocks = mutableMapOf<String, Block>()
	internal val blockClasses = mutableMapOf<KClass<out Block>, Block>()
	internal val blockItems = mutableMapOf<String, BlockItem>()
}

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object BlocksInit {
	init {
		// Discover block classes
		ReflectionHelper.forInstancesWithAnnotation(ModBlock::class, BlockBase::class, TechMod.PackagePrefix) { block, _ ->
			Blocks.blocks += Pair(block.apiName, block);
			Blocks.blockClasses += Pair(block.javaClass.kotlin, block);
		};
	}
	
	// region Block Registration
	
	@SubscribeEvent
	fun registerBlocks(event: RegistryEvent.Register<Block>) {
		Logger.info("Registering ${Blocks.blocks.size} blocks...");
		
		Blocks.blocks.values.forEach { event.registry.register(it) };
	}
	
	@SubscribeEvent
	fun registerBlockItems(event: RegistryEvent.Register<Item>) {
		val blocksWithItems = Blocks.blocks.filterValues { it.classHasAnnotation<HasBlockItem>() };
		
		Logger.info("Registering ${blocksWithItems.size} block items...");
		
		// Only block classes with @HasBlockItem directly applied to them will automatically receive BlockItems
		val blockItems = blocksWithItems.map { (name, block) ->
			val annotation = block.javaClass.getDeclaredAnnotation(HasBlockItem::class.java);
			val itemProperties = Item.Properties().group(TechModItemGroup).maxStackSize(annotation.maxStackSize);
			val blockItem = BlockItem(block, itemProperties).apply { registryName = block.registryName };
			
			Blocks.blockItems += Pair(name, blockItem);
			
			return@map blockItem;
		};
		
		event.registry.registerAll(*blockItems.toTypedArray());
	}
	
	// endregion Block Registration
}