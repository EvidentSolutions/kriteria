@file:OptIn(DelicateCriteriaApi::class)

package fi.evident.kriteria.expression

import fi.evident.kriteria.annotations.DelicateCriteriaApi
import fi.evident.kriteria.jpa.EntityMeta
import kotlin.reflect.KProperty1

/**
 * Represents a path to a property in an entity.
 *
 * Paths are used to reference properties in entities when building queries.
 */
public sealed class KrPath<T>() : KrExpression<T>() {

    /** Gets a basic (non-entity) property from this path. */
    public fun <X : Any> getBasic(property: KProperty1<out T, X?>): KrPropertyRef<X> =
        getBasicUnsafe(property.name)

    /**
     * Gets a basic (non-entity) property from this path using the property name.
     *
     * This is a low-level API that should be used with caution. It does not
     * verify that the property actually exists or has a correct type. Prefer
     * using the generated entity model or [getBasic] when possible.
     */
    @DelicateCriteriaApi
    public fun <X> getBasicUnsafe(name: String): KrPropertyRef<X> = KrPropertyRef(this, name)
}

/** Represents a path to a basic (non-entity) property. */
@ConsistentCopyVisibility
public data class KrPropertyRef<T> internal constructor(
    val childParent: KrPath<*>,
    val childProperty: String
) : KrPath<T>() {
    override fun toString(): String = "$childParent.$childProperty"
}

/** Represents a path to a many-to-one relationship property. */
@ConsistentCopyVisibility
public data class KrManyToOneRef<T> internal constructor(
    val childParent: KrFrom<*, *>,
    val childProperty: String
) : KrPath<T>() {
    override fun toString(): String = "$childParent.$childProperty"
}

/** Represents a reference to a collection property in an entity. */
public class KrCollectionRef<T> internal constructor(
    internal val childParent: KrFrom<*, *>,
    internal val childProperty: String
) {
    override fun toString(): String = "$childParent.$childProperty"
}

/** Represents a reference to a map property in an entity. */
@ConsistentCopyVisibility
public data class KrMapRef<K, V> internal constructor(
    internal val childParent: KrFrom<*, *>,
    internal val childProperty: String
) {
    override fun toString(): String = "$childParent.$childProperty"
}

/**
 * Base class for all path sources (roots or joins).
 */
public sealed class KrFrom<X, Y> : KrPath<Y>() {

    /**
     * Gets a reference to a collection property using the property name.
     *
     * This is a low-level API that should be used with caution. It does not
     * verify that the property actually exists or has a correct type.
     */
    @DelicateCriteriaApi
    public fun <X> getCollectionReferenceUnsafe(name: String): KrCollectionRef<X> =
        KrCollectionRef(this, name)

    /**
     * Gets a reference to a map property using the property name.
     *
     * This is a low-level API that should be used with caution. It does not
     * verify that the property actually exists or has a correct type.
     */
    @DelicateCriteriaApi
    public fun <K, V> getMapReferenceUnsafe(name: String): KrMapRef<K, V> = KrMapRef(this, name)

    /** Gets a many-to-one relationship property from this path. */
    public fun <T : Any> getManyToOne(property: KProperty1<out Y, T?>): KrManyToOneRef<T> =
        getManyToOneUnsafe(property.name)

    /**
     * Gets a many-to-one relationship property from this path using the property name.
     *
     * This is a low-level API that should be used with caution. It does not
     * verify that the property actually exists or has a correct type. Prefer
     * using the generated entity model or [getManyToOne] when possible.
     */
    @DelicateCriteriaApi
    public fun <T> getManyToOneUnsafe(name: String): KrManyToOneRef<T> = KrManyToOneRef(this, name)
}

/** Represents the root of a query path. */
public class KrRoot<T : Any> internal constructor(
    internal val rootEntityMeta: EntityMeta<T, *>,
) : KrFrom<T, T>() {
    override fun toString(): String = rootEntityMeta.toString()
}

internal sealed interface KrAnyJoin<X, Y>

/** Represents a join to an entity property. */
public class KrJoin<X, Y> internal constructor(
    internal val joinParent: KrFrom<*, out X>,
    internal val joinProperty: String,
    internal val joinType: KrJoinType,
    internal val joinFetch: Boolean,
) : KrFrom<X, Y>(), KrAnyJoin<Y, Y> {
    override fun toString(): String = "KrJoin($joinParent, $joinProperty, $joinType, $joinFetch)"
}

/** Represents a join to an entity property. */
public class KrPredicateJoin<X, Y : Any> internal constructor(
    internal val joinParent: KrFrom<*, out X>,
    internal val joinEntity: EntityMeta<Y, *>,
    internal val joinPredicate: (KrPath<Y>) -> KrPredicate,
    internal val joinType: KrJoinType,
) : KrFrom<X, Y>(), KrAnyJoin<Y, Y> {
    override fun toString(): String = "KrPredicateJoin($joinParent, $joinEntity, $joinType)"
}

/** Represents a join to a set property. */
public class KrSetJoin<X, Y> internal constructor(
    internal val joinParent: KrFrom<*, out X>,
    internal val joinProperty: String,
    internal val joinType: KrJoinType,
) : KrFrom<X, Y>(), KrAnyJoin<X, Y> {
    override fun toString(): String = "$joinParent.$joinProperty"
}

/** Represents a join to a map property. */
public class KrMapJoin<X, K, V> internal constructor(
    internal val joinParent: KrFrom<*, out X>,
    internal val joinProperty: String,
    internal val joinType: KrJoinType,
) : KrFrom<X, V>(), KrAnyJoin<X, V> {

    override fun toString(): String = "$joinParent.$joinProperty"

    /** A path to the key of the map entry. */
    public val key: KrPath<K> = MapKey(this)

    /** A path to the value of the map entry. */
    public val value: KrPath<V> = MapValue(this)

    internal class MapKey<K>(internal val join: KrMapJoin<*, K, *>) : KrPath<K>() {
        override fun toString(): String = "$join.key"
    }

    internal class MapValue<V>(internal val join: KrMapJoin<*, *, V>) : KrPath<V>() {
        override fun toString(): String = "$join.value"
    }
}

internal enum class KrJoinType {
    INNER, LEFT
}
