package fi.evident.kriteria.jpa

import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class EntityMetaTest {

    @Test
    fun `resolve identifier class`() {
        Assertions.assertEquals(Int::class, MyEntity.identifierClass)
    }

    private class MyEntity {
        val id: Int = 0

        companion object : EntityMeta<MyEntity, Int>(MyEntity::id)
    }
}
