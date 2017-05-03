package me.arcanox.techmod.util

import me.arcanox.techmod.TechMod
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLLog
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger

object Logger {
	var logger = LogManager.getLogger(TechMod.Name, FMLLog.getLogger().messageFactory);
	var marker = MarkerManager.getMarker(TechMod.Name);
	
	fun debug(message: Any) = logger.debug(marker, message);
	fun info(message: Any) = logger.info(marker, message);
	fun warn(message: Any) = logger.warn(marker, message);
	fun error(message: Any) = logger.error(marker, message);
	fun fatal(message: Any) = logger.fatal(marker, message);
}
