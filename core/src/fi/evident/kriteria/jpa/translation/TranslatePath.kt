package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.*
import jakarta.persistence.criteria.Path

/**
 * Translates paths into corresponding JPA paths.
 *
 * Assumes that all roots are joins are already present in the context and creates new
 * paths only for the children.
 *
 * The translation context has a cache of paths so that for each logical path,
 * the same instance is always returned.
 */
context(ctx: TranslationContext)
internal fun <T> KrPath<T>.translatePath(): Path<T> = when (this) {
    is KrRoot<*> -> ctx.pathCache.resolveRoot(this).castPath()
    is KrAnyJoin<*, *> -> ctx.pathCache.resolveJoin(this).castPath()
    is KrMapJoin.MapKey -> ctx.pathCache.resolveMapJoin(join).key()
    is KrMapJoin.MapValue -> ctx.pathCache.resolveMapJoin(join).value()
    is KrBasicPropertyRef -> ctx.pathCache.getOrPutProperty(this) {
        childParent.translatePath().get(childProperty)
    }
    is KrManyToOneRef -> ctx.pathCache.getOrPutManyToOne(this) {
        childParent.translatePath().get(childProperty)
    }
    is KrOneToOneRef -> ctx.pathCache.getOrPutOneToOne(this) {
        childParent.translatePath().get(childProperty)
    }
}
