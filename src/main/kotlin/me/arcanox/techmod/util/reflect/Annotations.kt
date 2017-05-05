package me.arcanox.techmod.util.reflect

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

// region Blocks

@Target(AnnotationTarget.CLASS)
annotation class ModBlock

@Target(AnnotationTarget.CLASS)
annotation class HasItemBlock

@Target(AnnotationTarget.CLASS)
annotation class HasItemModel

// endregion

// region TileEntities

@Target(AnnotationTarget.CLASS)
annotation class ModTileEntity(val id: String)

@Target(AnnotationTarget.CLASS)
annotation class HasTESR(val tesrName: String)

// endregion