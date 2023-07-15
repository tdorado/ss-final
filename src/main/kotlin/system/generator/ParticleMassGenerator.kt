package system.generator

data class ParticleMassGenerator(val lowDiameter: Double, val lowMass: Double) {

    fun getParticleMass(diameter: Double): Double = diameter * lowMass / lowDiameter

}
