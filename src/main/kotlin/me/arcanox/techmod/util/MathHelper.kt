package me.arcanox.techmod.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.math.vector.Vector3i


fun BlockPos.toVec3d() = Vector3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble());
fun BlockPos.toVec3i() = Vector3i(this.x, this.y, this.z);