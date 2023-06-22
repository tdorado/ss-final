package engine

interface FileGenerator {
    fun addToFile(particles: List<Particle>, time: Double)
    fun closeFile()
}