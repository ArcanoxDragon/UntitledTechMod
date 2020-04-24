package me.arcanox.techmod.util

fun <T> lazyCache(valueSupplier: () -> T) = LazyCache(valueSupplier);

class LazyCache<T>(private val valueSupplier: () -> T) {
	private var internalValue: T? = null;
	
	var initialized: Boolean = false
		private set;
	
	val value: T
		get() {
			if (!this.initialized) {
				this.internalValue = this.valueSupplier();
				this.initialized = true;
			}
			
			return this.internalValue!!;
		}
	
	fun invalidate() {
		this.initialized = false;
		this.internalValue = null;
	}
}