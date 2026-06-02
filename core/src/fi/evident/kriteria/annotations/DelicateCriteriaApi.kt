package fi.evident.kriteria.annotations

/**
 * Marks an API as delicate, requiring careful use.
 *
 * APIs marked with this annotation are typically low-level or unsafe and should be used
 * with caution. Users of these APIs should fully read and understand the documentation
 * of the marked declaration before using it.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This is a delicate API and its use requires care." +
            " Make sure you fully read and understand documentation of the declaration that is marked as a delicate API.",
)
public annotation class DelicateCriteriaApi
