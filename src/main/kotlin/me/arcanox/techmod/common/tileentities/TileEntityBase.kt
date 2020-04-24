package me.arcanox.techmod.common.tileentities

import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SUpdateTileEntityPacket
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType

abstract class TileEntityBase(type: TileEntityType<out TileEntity>) : TileEntity(type) {
	override fun getUpdatePacket(): SUpdateTileEntityPacket? {
		val root = this.updateTag;
		return SUpdateTileEntityPacket(this.pos, 1, root);
	}
	
	override fun onDataPacket(net: NetworkManager, pkt: SUpdateTileEntityPacket) {
		val root = pkt.nbtCompound;
		this.handleUpdateTag(root);
	}
}