package me.arcanox.techmod

import me.arcanox.techmod.api.API
import me.arcanox.techmod.api.Constants
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

object CreativeTab : CreativeTabs(CreativeTabs.getNextID(), TechMod.ModID) {
	override fun getTabIconItem(): ItemStack = API.blocks().getBlockItemStack(Constants.Blocks.AutomaticDoor, 1);
}
