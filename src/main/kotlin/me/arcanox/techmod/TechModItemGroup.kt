package me.arcanox.techmod

import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.common.blocks.Blocks
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object TechModItemGroup : ItemGroup(TechMod.ModID) {
	override fun createIcon(): ItemStack = Blocks.getBlockItemStack(Constants.Blocks.AutomaticDoor, 1);
}
