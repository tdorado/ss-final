package engine

import engine.model.Particle

interface FileGenerator {
    fun addToFile(particles: Set<Particle>, time: Double)
    fun closeFile()
}