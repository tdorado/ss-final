package system.particle_generators

data class ParticleMassGenerator(val lowDiameter: Double, val lowMass: Double) {

    fun getParticleMass(diameter: Double): Double = diameter * lowMass / lowDiameter

}
