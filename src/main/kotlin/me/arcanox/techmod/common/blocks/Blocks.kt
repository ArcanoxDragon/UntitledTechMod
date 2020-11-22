package me.arcanox.techmod.common.blocks

import me.arcanox.techmod.TechMod
import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.common.items.Items
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
internal object Blocks {
	// region Holder fields
	
	internal val registry = DeferredRegister.create(ForgeRegistries.BLOCKS, TechMod.ModID)!!
	private val blocks = mutableMapOf<String, RegistryObject<out Block>>()
	private val blockItems = mutableMapOf<String, RegistryObject<out Item>>()
	
	// endregion Holder fields
	
	// region Block fields
	
	val automaticDoor = register(Constants.Blocks.AutomaticDoor) { AutomaticDoorBlock }.withBlockItem()
	
	// endregion Block fields
	
	fun register(eventBus: IEventBus) = registry.register(eventBus)
	
	fun getBlock(name: String) =
		if (name in blocks)
			blocks[name]!!.get()
		else
			throw UnsupportedOperationException("Unknown Block: $name")
	
	fun getBlockItemStack(name: String, count: Int) =
		if (name in blockItems)
			ItemStack(blockItems[name]!!.get(), count)
		else
			throw UnsupportedOperationException("Unknown BlockItem: $name")
	
	// region Registry helper methods
	
	private fun <T : Block> register(name: String, supplier: () -> T) = registry.register(name, supplier).also { blocks += name to it }!!
	
	private fun <T : Block> RegistryObject<T>.withBlockItem(itemConfigurator: (Item.Properties) -> Item.Properties = { it }): RegistryObject<BlockItem> {
		val name = this.id.path;
		
		return Items.registry.register(name) { BlockItem(this.get(), itemConfigurator(Item.Properties())) }.also { blockItems += name to it }
	}
	
	// endregion Registry helper methods
}