package system

import engine.model.Vector
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CannonballParticleGeneratorTest {

    // Este test tarda en correr en mi pc 2min 16segundos (generacion + chequeo, la generacion tarda mucho menos)
    @Test
    fun testNoOverlap_100k() {
        // Configura el entorno de prueba
        val minRadius = 0.02 / 1000
        val maxRadius = 0.05 / 1000
        val boxSizeInMeters = 1.0
        val boxVector = Vector(1.0, 1.0, 1.0)
        val numberOfParticles = 100000

        val walls = listOf(
            Wall(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), boxSizeInMeters, boxSizeInMeters),
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), boxSizeInMeters, boxSizeInMeters),
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 1.0), boxSizeInMeters, boxSizeInMeters)
        )

        val generator = CannonballParticleGenerator(
            minRadius,
            maxRadius,
            boxVector,
            numberOfParticles,
            walls,
            ParticleMassGenerator(0.0, 0.01),
            1.0,
            1.0
        )

        // Genera las partículas
        val particles = generator.generateParticles()

        // Verifica que no haya superposiciones
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                assertTrue(!particles[i].overlapsWith(particles[j].position, particles[j].radius))
            }
            assertTrue(walls.none { it.overlapsWith(particles[i].position, particles[i].radius) })
        }
    }


    // Este test tarda en correr en mi pc 10min 5segundos (generacion + chequeo, la generacion tarda mucho menos)
    @Test
    fun testNoOverlap_200k() {
        // Configura el entorno de prueba
        val minRadius = 0.02 / 1000
        val maxRadius = 0.05 / 1000
        val boxSizeInMeters = 1.0
        val boxVector = Vector(1.0, 1.0, 1.0)
        val numberOfParticles = 200000

        val walls = listOf(
            Wall(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), boxSizeInMeters, boxSizeInMeters),
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), boxSizeInMeters, boxSizeInMeters),
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 1.0), boxSizeInMeters, boxSizeInMeters)
        )

        val generator =
            CannonballParticleGenerator(
                minRadius,
                maxRadius,
                boxVector,
                numberOfParticles,
                walls,
                ParticleMassGenerator(0.0, 0.01),
                1.0,
                0.4
            )

        // Genera las partículas
        val particles = generator.generateParticles()

        // Verifica que no haya superposiciones
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                assertTrue(!particles[i].overlapsWith(particles[j].position, particles[j].radius))
            }
            assertTrue(walls.none { it.overlapsWith(particles[i].position, particles[i].radius) })
        }
    }
}
