package me.arcanox.techmod.common.blocks.base

import me.arcanox.techmod.CreativeTab
import me.arcanox.techmod.util.Logger
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkCache
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk


abstract class BlockBase(name: String, material: Material) : Block(material) {
	var apiName: String
		private set
	
	init {
		this.apiName = name;
		this.creativeTab = CreativeTab;
		
		this.setRegistryName(name);
	}
	
	fun getTileEntitySafe(world: IBlockAccess, pos: BlockPos, state: IBlockState): TileEntity? {
		if (!this.hasTileEntity(state)) {
			Logger.error("Cannot get TileEntity for block ${this.registryName} because it does not have one")
			return null;
		}
		
		val tileEntity = when (world) {
			is ChunkCache -> world.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK)
			else          -> world.getTileEntity(pos)
		}
		
		if (tileEntity == null) {
			Logger.warn("Block ${this.registryName} at position $pos should have a TileEntity but it does not!");
			
			return when (world) {
				is World -> this.createTileEntity(world, state)!!.also { world.setTileEntity(pos, it) };
				else     -> throw IllegalStateException("Block ${this.registryName} at position $pos should have a TileEntity but it does not, and a TileEntity could not be created!")
			}
		}
		
		return tileEntity;
	}
	
	override fun getTranslationKey() = "tile.${this.registryName}";
}