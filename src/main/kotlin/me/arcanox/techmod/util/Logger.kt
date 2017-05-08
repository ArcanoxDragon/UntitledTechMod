package me.arcanox.techmod.util

import me.arcanox.techmod.TechMod
import me.arcanox.techmod.common.proxy.IInitStageHandler
import me.arcanox.techmod.util.reflect.InitHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.MarkerManager

@InitHandler(priority = 0 /* before everything */)
object Logger : IInitStageHandler {
	val marker = MarkerManager.getMarker(TechMod.ModID.toUpperCase())!!;
	lateinit var logger: Logger;
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		this.logger = e.modLog;
	}
	
	internal fun format(message: Any) = "[${TechMod.Name}] $message";
	
	fun debug(message: Any) = logger.debug(marker, this.format(message));
	fun info(message: Any) = logger.info(marker, this.format(message));
	fun warn(message: Any) = logger.warn(marker, this.format(message));
	fun error(message: Any) = logger.error(marker, this.format(message));
	fun fatal(message: Any) = logger.fatal(marker, this.format(message));
}

