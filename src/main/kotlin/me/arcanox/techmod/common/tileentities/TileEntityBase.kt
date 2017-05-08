package me.arcanox.techmod.common.tileentities

import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity


abstract class TileEntityBase : TileEntity() {
	override fun getUpdatePacket(): SPacketUpdateTileEntity? {
		val root = this.updateTag;
		return SPacketUpdateTileEntity(this.pos, 1, root);
	}
	
	override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
		val root = pkt.nbtCompound;
		this.handleUpdateTag(root);
	}
}