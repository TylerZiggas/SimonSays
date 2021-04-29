package edu.umsl.tyler

import java.lang.ref.WeakReference
import kotlin.reflect.KClass

class ModelHolder private constructor() {

    private val modelGames = HashMap<String, WeakReference<Any?>>()

    companion object{
        @JvmStatic
        val instance = ModelHolder()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> get(classType: KClass<T>): T? {
        val modelPlayer = modelGames[classType.java.toString()]
        return modelPlayer?.get() as? T
    }

    fun <T: Any> set(classInstance: T?) {
        modelGames[classInstance?.javaClass.toString()] = WeakReference(classInstance as? Any)
    }
}