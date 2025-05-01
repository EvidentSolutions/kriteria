package fi.evident.kriteria.test.db

import fi.evident.kriteria.test.DefaultTestData
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

@ExtendWith(DatabaseContextResolver::class)
annotation class DatabaseTest

class DatabaseContextResolver : ParameterResolver {

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        val parameterType = parameterContext.parameter.type
        return parameterType.isAssignableFrom(DatabaseContext::class.java)
                || parameterType.isAssignableFrom(DefaultTestData::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val parameterType = parameterContext.parameter.type

        return when {
            parameterType.isAssignableFrom(DatabaseContext::class.java) -> databaseContext
            parameterType.isAssignableFrom(DefaultTestData::class.java) -> {
                val testData = DefaultTestData.applyTo(databaseContext)
                extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).put("testData", testData)
                return testData
            }
            else -> error("unsupported parameter type: $parameterType")
        }
    }

    companion object {
        private val databaseContext by lazy {
            DatabaseContext(buildSessionFactory())
        }
    }
}

