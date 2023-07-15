package system.particle_generators

import kotlin.random.Random

data class ParticleDiameterGenerator(val startInterval: Double, val endInterval: Double) {
    fun getParticleDiameter(): Double = Random.nextDouble(startInterval, endInterval)

}
