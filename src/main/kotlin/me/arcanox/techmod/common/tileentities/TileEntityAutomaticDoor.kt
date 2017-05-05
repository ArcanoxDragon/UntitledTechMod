package me.arcanox.techmod.common.tileentities

import me.arcanox.techmod.api.Constants
import me.arcanox.techmod.util.reflect.HasTESR
import me.arcanox.techmod.util.reflect.ModTileEntity
import net.minecraft.tileentity.TileEntity

@ModTileEntity(Constants.Blocks.AutomaticDoor)
@HasTESR("TESRAutomaticDoor")
class TileEntityAutomaticDoor : TileEntity() {
}