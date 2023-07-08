package system

import kotlin.random.Random

data class ParticleDiameterGenerator(val startInterval: Double, val endInterval: Double) {
    fun getParticleDiameter(model: MassGeneratorModel = MassGeneratorModel.UNIFORM): Double =
        when (model) {
            MassGeneratorModel.UNIFORM -> Random.nextDouble(startInterval, endInterval)
        }
}

enum class MassGeneratorModel {
    UNIFORM
}