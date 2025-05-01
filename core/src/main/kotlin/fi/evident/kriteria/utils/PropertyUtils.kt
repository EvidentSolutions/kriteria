package fi.evident.kriteria.utils

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal val <T : Any> KProperty1<in T, *>.receiverClass: KClass<T>
    get() {
        val receiver = parameters.firstOrNull() ?: error("no receiver for property $this")
        @Suppress("UNCHECKED_CAST")
        return receiver.type.classifier as KClass<T>
    }
