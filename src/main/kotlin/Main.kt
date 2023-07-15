import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import system.CannonballSystem

@Command(name = "Main", mixinStandardHelpOptions = true, version = ["1.0"])
class Main : Runnable {

    @Option(names = ["-dt"], description = ["Time delta in seconds"], required = false)
    var timeDelta: Double = 0.00005

    @Option(names = ["-dt2"], description = ["Save time delta in seconds"], required = false)
    var saveTimeDelta: Double = 0.001

    @Option(names = ["-ct"], description = ["Simulation cutoff time in seconds"], required = false)
    var cutoffTime: Double = 3.0

    @Option(names = ["-o"], description = ["Output file name"], required = false)
    var outputFileName: String = defaultOutputFile()

    @Option(names = ["-pFile"], description = ["File path to load particles from it"], required = false)
    var pFile: String = ""

    @Option(names = ["-pGen"], description = ["True to run with particle generator"], required = false)
    var pGen: Boolean = true

    @Option(names = ["-pStableTime"], description = ["Time to cut for particles stabilization"], required = false)
    var pStabilizationTime: Double = 0.7

    @Option(
        names = ["-pStableEnergy"],
        description = ["Kinetic energy to cut for particles stabilization"],
        required = false
    )
    var pStabilizationEnergy: Double = 5E-3

    @Option(names = ["-n"], description = ["Number of particles"], required = false)
    var nParticles: Int = 2000

    @Option(names = ["-pMass"], description = ["Mass of the lowest radius particle"], required = false)
    var pMass: Double = 0.085

    @Option(names = ["-pld"], description = ["Lower bound of particle's diameter"], required = false)
    var pLowDiam: Double = 0.015

    @Option(names = ["-pud"], description = ["Upper bound of particle's diameter"], required = false)
    var pUpperDiam: Double = 0.03

    @Option(names = ["-pKn"], description = ["Particles Kn variable"], required = false)
    var pKn: Double = 2E6

    @Option(names = ["-pKt"], description = ["Particles Kt variable"], required = false)
    var pKt: Double = 2 * pKn

    @Option(names = ["-pGamma"], description = ["Particles gamma variable"], required = false)
    var pGamma: Double = 20.0

    @Option(names = ["-wallKn"], description = ["Walls Kn variable"], required = false)
    var wallKn: Double = 3E3

    @Option(names = ["-wallKt"], description = ["Walls Kt variable"], required = false)
    var wallKt: Double = 2 * wallKn

    @Option(names = ["-wallGamma"], description = ["Particles wall variable"], required = false)
    var wallGamma: Double = 20.0

    @Option(names = ["-ballMass"], description = ["Mass of the lowest radius particle"], required = false)
    var ballMass: Double = 17.5

    @Option(names = ["-ballKn"], description = ["Cannonball Kn variable"], required = false)
    var ballKn: Double = pKn

    @Option(names = ["-ballKt"], description = ["Cannonball Kt variable"], required = false)
    var ballKt: Double = 2 * pKn

    @Option(names = ["-ballGamma"], description = ["Cannonball gamma variable"], required = false)
    var ballGamma: Double = 70.0

    @Option(names = ["-ballAngle"], description = ["Cannonball angle variable"], required = false)
    var ballAngle: Double = 90.0

    @Option(names = ["-ballVelocity"], description = ["Cannonball velocity variable"], required = false)
    var ballVelocity: Double = 70.0

    @Option(names = ["-ballDiameter"], description = ["Cannonball diameter variable"], required = false)
    var ballDiameter: Double = 175e-3

    @Option(names = ["-ballHeight"], description = ["Cannonball height variable"], required = false)
    var ballHeight: Double = 0.6

    @Option(names = ["-bw"], description = ["Box width in meters"], required = false)
    var boxWidth: Double = 0.4

    @Option(names = ["-bh"], description = ["Box height in meters"], required = false)
    var boxHeight: Double = 1.0

    override fun run() {
        val cannonballSystem = CannonballSystem(
            timeDelta = timeDelta,
            saveTimeDelta = saveTimeDelta,
            cutoffTime = cutoffTime,
            boxHeight = boxHeight,
            boxWidth = boxWidth,
            numberOfParticles = nParticles,
            minParticleDiameter = pLowDiam,
            maxParticleDiameter = pUpperDiam,
            lowParticleMass = pMass,
            pKn = pKn,
            pKt = pKt,
            pGamma = pGamma,
            wallKn = wallKn,
            wallKt = wallKt,
            wallGamma = wallGamma,
            cannonballKn = ballKn,
            cannonballKt = ballKt,
            cannonballGamma = ballGamma,
            cannonballAngle = ballAngle,
            cannonballVelocity = ballVelocity,
            cannonballMass = ballMass,
            cannonballDiameter = ballDiameter,
            cannonballHeight = ballHeight,
            pFile = pFile,
            outputFile = outputFileName,
            pStableEnergy = pStabilizationEnergy,
            pStableTime = pStabilizationTime,
        )

        cannonballSystem.run()
    }

    private fun defaultOutputFile(): String {
        return "nP:$nParticles" +
                "_pMass:$pMass" +
                "_minDiameter:$pLowDiam" +
                "_maxDiameter:$pUpperDiam" +
                "_angle:$ballAngle" +
                "_pKt:$pKt" +
                "_pGamma:$pGamma" +
                "_cutoff:$cutoffTime" +
                "_dT:$timeDelta" +
                "_dT2:$saveTimeDelta" +
                "_wallGamma${wallGamma}"
    }
}

fun main(args: Array<String>) {
    val commandLine = CommandLine(Main())
    commandLine.execute(*args)
}