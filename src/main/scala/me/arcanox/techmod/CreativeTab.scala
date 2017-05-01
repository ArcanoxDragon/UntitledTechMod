package me.arcanox.techmod

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

object CreativeTab extends CreativeTabs( CreativeTabs.getNextID(), TechMod.NAME ) {
	override def getTabIconItem: ItemStack = null;
}
