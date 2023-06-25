package engine

import engine.model.Particle

interface FileGenerator {
    fun addToFile(particles: List<Particle>, time: Double)
    fun closeFile()
}