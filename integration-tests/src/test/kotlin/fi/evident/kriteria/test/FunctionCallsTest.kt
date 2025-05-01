@file:OptIn(DelicateCriteriaApi::class)

package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.annotations.DelicateCriteriaApi
import fi.evident.kriteria.expression.callFunction
import fi.evident.kriteria.expression.literal
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.evaluateExpression
import fi.evident.kriteria.test.db.transactionalTest
import kotlin.test.Test

@DatabaseTest
class FunctionCallsTest(private val db: DatabaseContext) {

    @Test
    fun `find by one-to-many`() = transactionalTest(db) {
        val result = evaluateExpression {
            callFunction<String>("upper", literal("foo"))
        }

        assertThat(result).isEqualTo("FOO")
    }
}
