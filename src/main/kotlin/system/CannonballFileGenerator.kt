package system

import engine.FileGenerator
import engine.model.Particle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


class CannonballFileGenerator(folder: String, filename: String) : FileGenerator {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val bw: BufferedWriter
    private lateinit var fw: FileWriter

    init {
        try {
            val directory = File(folder)
            if (!directory.exists()) {
                directory.mkdir()
            }
            var file = File("$folder$filename.xyz")
            if (file.exists()) {
                file.renameTo(File("$folder$filename.old.xyz"))
                logger.info("Existing file renamed to: $folder$filename.old")
            }
            file = File("$folder$filename.xyz")
            fw = FileWriter(file, false)
        } catch (e: IOException) {
            logger.error("Error while creating or renaming file: $e")
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
            bw.flush()
        } catch (e: IOException) {
            logger.error("Error while adding to file: $e")
        }
    }

    override fun closeFile() {
        try {
            bw.close()
        } catch (e: IOException) {
            logger.error("Error while closing file: $e")
        }
    }
}