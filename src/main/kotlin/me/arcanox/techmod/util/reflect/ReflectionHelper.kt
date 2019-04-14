package me.arcanox.techmod.util.reflect

import me.arcanox.techmod.util.Logger
import net.minecraftforge.fml.common.discovery.ASMDataTable
import java.lang.reflect.AnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

inline fun <reified T : Annotation> AnnotatedElement.hasAnnotation(declaredOnly: Boolean = true): Boolean = when {
	declaredOnly -> this.getDeclaredAnnotation(T::class.java)
	else         -> this.getAnnotation(T::class.java)
} != null

inline fun <reified T : Annotation> KClass<*>.hasAnnotation(declaredOnly: Boolean = true): Boolean = this.java.hasAnnotation<T>(declaredOnly)

inline fun <reified T : Annotation> Any.classHasAnnotation(declaredOnly: Boolean = true): Boolean
	= this.javaClass.hasAnnotation<T>(declaredOnly)

object ReflectionHelper {
	fun <C, A : Annotation> getClassesWithAnnotation(asmData: ASMDataTable,
	                                                 annotationClass: Class<A>,
	                                                 supertype: Class<C>): List<Pair<Class<out C>, A>> {
		val asmClasses = asmData.getAll(annotationClass.canonicalName);
		return asmClasses.map {
			val discoveredClass = Class.forName(it.className);
			
			if (discoveredClass == null) {
				Logger.warn("Could not retrieve class handle for class in ASM data table: ${it.className}")
				return@map null;
			}
			
			val typed = discoveredClass.asSubclass(supertype);
			
			if (typed == null) {
				Logger.warn("Found a class with annotation ${annotationClass.canonicalName} of type ${discoveredClass.canonicalName}, which cannot be converted to requested type ${supertype.canonicalName}");
				return@map null;
			}
			
			val annotation = typed.getDeclaredAnnotation(annotationClass);
			
			if (annotation == null) {
				Logger.warn("Class ${it.className} has annotation ${annotationClass.canonicalName} through inheritance, but the annotation is not declared explicitly on the class");
				return@map null;
			}
			
			return@map Pair(typed, annotation);
		}.filterNotNull();
	}
	
	fun <C, A : Annotation> getInstancesWithAnnotation(asmData: ASMDataTable,
	                                                   annotationClass: Class<A>,
	                                                   supertype: Class<C>,
	                                                   new: (Class<out C>) -> C = { it.newInstance() }): List<Pair<C, A>> {
		return getClassesWithAnnotation(asmData, annotationClass, supertype).map { (it, annotation) ->
			try {
				val instance = new(it);
				
				return@map Pair(instance, annotation);
			} catch (ex: Exception) {
				Logger.error("Error instantiating class from ASM data table: ${it.canonicalName}");
				ex.printStackTrace();
			}
			
			return@map null;
		}.filterNotNull();
	}
	
	fun <C : Any, A : Annotation> getClassesWithAnnotation(asmData: ASMDataTable,
	                                                       annotationClass: KClass<A>,
	                                                       supertype: KClass<out C>): List<Pair<KClass<out C>, A>>
		= getClassesWithAnnotation(asmData, annotationClass.java, supertype.java).map { (c, a) -> Pair(c.kotlin, a) }
	
	fun <C : Any, A : Annotation> getInstancesWithAnnotation(asmData: ASMDataTable,
	                                                         annotationClass: KClass<A>,
	                                                         supertype: KClass<C>,
	                                                         new: (KClass<out C>) -> C = { it.objectInstance ?: it.createInstance() }): List<Pair<C, A>>
		= getInstancesWithAnnotation(asmData, annotationClass.java, supertype.java) { jc -> new(jc.kotlin) }
}