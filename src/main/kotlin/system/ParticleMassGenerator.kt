package system

import kotlin.random.Random

data class ParticleMassGenerator(val startInterval: Double, val endInterval: Double) {
    fun getParticleMass(model: MassGeneratorModel = MassGeneratorModel.UNIFORM): Double =
        when (model) {
            MassGeneratorModel.UNIFORM -> Random.nextDouble(startInterval, endInterval)
        }
}

enum class MassGeneratorModel {
    UNIFORM
}