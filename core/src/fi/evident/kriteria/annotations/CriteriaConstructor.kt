package fi.evident.kriteria.annotations

/**
 * Marks a constructor that should have a Criteria-compatible wrapper function generated for it.
 *
 * The wrapper function will be named "construct" + the simple name of the class, e.g., "constructFoo" for a class "Foo".
 * This provides better type safety than using the generic `construct(Foo::class.java, ...)` function.
 */
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
public annotation class CriteriaConstructor
