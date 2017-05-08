package me.arcanox.techmod.util.reflect

// region Initialization

@Target(AnnotationTarget.CLASS)
annotation class InitHandler(val priority: Int = 5)

@Target(AnnotationTarget.CLASS)
annotation class ClientInitHandler(val priority: Int = 5)

// endregion

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