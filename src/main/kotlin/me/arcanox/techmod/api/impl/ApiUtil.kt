package me.arcanox.techmod.api.impl

import me.arcanox.techmod.api.ITechModApi
import me.arcanox.techmod.api.TechModApi

/**
 * Package-internal shorthand for accessing API instance
 */
internal fun api(): ITechModApi = TechModApi.getInstance()