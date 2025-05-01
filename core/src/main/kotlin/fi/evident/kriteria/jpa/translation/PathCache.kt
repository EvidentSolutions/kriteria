package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.*
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.MapJoin
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Root

/**
 * Makes sure that each path is resolved to a unique JPA-path.
 */
internal class PathCache private constructor() {

    private val roots = mutableMapOf<KrRoot<*>, Path<*>>()
    private val manyToOneRefs = mutableMapOf<KrManyToOneRef<*>, Path<*>>()
    private val propertyRefs = mutableMapOf<KrPropertyRef<*>, Path<*>>()
    private val joins = mutableMapOf<KrAnyJoin<*, *>, From<*, *>>()

    fun addRoot(root: KrRoot<*>, jpaRoot: Root<*>) {
        val oldValue = roots.putIfAbsent(root, jpaRoot)
        check(oldValue == null) { "duplicate root: $root" }
    }

    fun addJoin(join: KrAnyJoin<*, *>, jpaJoin: From<*, *>) {
        val oldValue = joins.putIfAbsent(join, jpaJoin)
        check(oldValue == null) { "duplicate root: $join" }
    }

    fun <T> getOrPutManyToOne(path: KrManyToOneRef<T>, func: () -> Path<T>): Path<T> =
        manyToOneRefs.getOrPut(path) { func() }.castPath()

    fun <T> getOrPutProperty(path: KrPropertyRef<T>, func: () -> Path<T>): Path<T> =
        propertyRefs.getOrPut(path) { func() }.castPath()

    @Suppress("UNCHECKED_CAST")
    private fun <X, Y> resolveAnyJoin(path: KrAnyJoin<X, Y>): From<X, Y> =
        (joins[path] ?: error("no path '$path' in path-cache")) as From<X, Y>

    @Suppress("UNCHECKED_CAST")
    fun <X, Y> resolveJoin(path: KrAnyJoin<X, Y>): From<X, Y> =
        resolveAnyJoin(path)

    fun <X, T> resolveFrom(path: KrFrom<X, T>): From<X, T> {
        val result: From<*, *> = when (path) {
            is KrRoot<*> -> resolveRoot(path)
            is KrAnyJoin<*, *> -> resolveAnyJoin(path)
        }

        @Suppress("UNCHECKED_CAST")
        return result as From<X, T>
    }

    @Suppress("UNCHECKED_CAST")
    fun <K, V> resolveMapJoin(path: KrMapJoin<*, K, V>): MapJoin<*, K, V> =
        resolveAnyJoin(path) as MapJoin<*, K, V>

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> resolveRoot(path: KrRoot<T>): Root<T> =
        roots[path] as Root<T>

    companion object {
        fun build(init: PathCache.() -> Unit): PathCache {
            val cache = PathCache()
            cache.init()
            return cache
        }

        fun <T : Any> singleRoot(root: KrRoot<T>, jpaRoot: Root<T>): PathCache = build {
            addRoot(root, jpaRoot)
        }
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T> Path<*>.castPath(): Path<T> = this as Path<T>
