package me.arcanox.techmod

import me.arcanox.techmod.api.API
import me.arcanox.techmod.api.Constants
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack;

object ItemGroup : ItemGroup(TechMod.ModID) {
	override fun createIcon(): ItemStack = API.getInstance().blocks().getBlockItemStack(Constants.Blocks.AutomaticDoor, 1);
}
