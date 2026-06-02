package fi.evident.kriteria.ksp

internal class ProcessorConfiguration(options: Map<String, String>) {
    val targetPackage = options["kriteriaProcessorTargetPackage"] ?: "kriteria.gen"
}
