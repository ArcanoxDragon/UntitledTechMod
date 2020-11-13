package me.arcanox.techmod

import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.api.impl.api
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object TechModItemGroup : ItemGroup(TechMod.ModID) {
	override fun createIcon(): ItemStack = api().blocks().getBlockItemStack(Constants.Blocks.AutomaticDoor, 1);
}
