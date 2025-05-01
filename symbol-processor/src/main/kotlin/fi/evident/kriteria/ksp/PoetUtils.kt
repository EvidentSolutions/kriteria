package fi.evident.kriteria.ksp

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.PropertySpec
import jakarta.persistence.*

fun attributeOverrides(vararg overrides: Pair<String, String>): AnnotationSpec {
    val result = AnnotationSpec.builder(AttributeOverrides::class)
    for ((name, column) in overrides)
        result.addMember("%L", AnnotationSpec.builder(AttributeOverride::class)
            .addMember("name = %S", name)
            .addMember("column = %L", columnAnnotation(column))
            .build()
        )
    return result.build()
}

fun columnAnnotation(name: String): AnnotationSpec =
    AnnotationSpec.builder(Column::class).addMember("name = %S", name).build()

fun manyToOne(name: String, type: ClassName, initializer: String? = null): PropertySpec {
    val spec = PropertySpec.builder(name, type)
        .addAnnotation(AnnotationSpec.builder(ManyToOne::class).addMember("fetch = %T.LAZY", FetchType::class).build())
        .addAnnotation(AnnotationSpec.builder(JoinColumn::class).addMember("insertable = false, updatable = false").build())
    if (initializer != null)
        spec.initializer(initializer)
    return spec.build()
}
