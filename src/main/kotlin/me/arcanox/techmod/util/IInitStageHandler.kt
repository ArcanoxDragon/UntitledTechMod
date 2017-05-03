package me.arcanox.techmod.util

internal interface IInitStageHandler {
	fun onPreInit() {}
	fun onInit() {}
	fun onPostInit() {}
}