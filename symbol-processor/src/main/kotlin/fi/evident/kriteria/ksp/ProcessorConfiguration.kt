package fi.evident.kriteria.ksp

class ProcessorConfiguration(options: Map<String, String>) {
    val targetPackage = options["kriteriaProcessorTargetPackage"] ?: "kriteria.gen"
}
