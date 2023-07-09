package system

import engine.FileGenerator
import engine.model.Particle
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


class CannonballFileGenerator(filename: String) : FileGenerator {
    companion object {
        private const val folder = "out/"
    }

    private val bw: BufferedWriter
    private lateinit var fw: FileWriter

    init {
        try {
            val directory = File(folder)
            if (!directory.exists()) {
                directory.mkdir()
            }
            val pw = FileWriter("$folder$filename.xyz")
            pw.close()
            fw = FileWriter("$folder$filename.xyz", true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        bw = BufferedWriter(fw)
    }

    override fun addToFile(particles: Set<Particle>, time: Double) {
        try {
            bw.write(particles.size.toString() + "\n")
            bw.write("id xPosition yPosition zPosition xVelocity yVelocity zVelocity radius mass pressure time\n")
            for (particle in particles) {
                bw.write(
                    particle.id.toString() + " " +
                            particle.position.x.toString() + " " +
                            particle.position.y.toString() + " " +
                            particle.position.z.toString() + " " +
                            particle.velocity.x.toString() + " " +
                            particle.velocity.y.toString() + " " +
                            particle.velocity.z.toString() + " " +
                            particle.radius.toString() + " " +
                            particle.mass.toString() + " " +
                            particle.pressure.toString() + " " +
                            time.toString() + "\n"
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun closeFile() {
        try {
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}