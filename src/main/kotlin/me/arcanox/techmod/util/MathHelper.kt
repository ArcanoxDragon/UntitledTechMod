package me.arcanox.techmod.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i


fun BlockPos.toVec3d() = Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble());
fun BlockPos.toVec3i() = Vec3i(this.x, this.y, this.z);