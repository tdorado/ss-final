# ss-final

This project simulates and analyzes a cannonball falling onto a granular field.

## Prerequisites

Ensure you have Java 17 and Python installed on your system. If you don't, you can download them and follow the installation instructions from the following links:

- [Java 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Python](https://www.python.org/downloads/)

Make sure to set Java 17 as your default Java Development Kit.

Also, ensure to have the Python dependencies installed. To install execute:

```
pip install -r post-processing/requirements.txt
```

## How to Build the Project

1. Clone the repository:
    ```
    git clone git@github.com:tdorado/ss-final.git
    ```
2. Navigate into the project folder:
    ```
    cd ss-final
    ```
3. Build the project using Gradle:
    ```
    ./gradlew build
    ```

## How to Build the simulator

First build the project using Gradle:
```
./gradlew build
```

## How to Run the simulator

The program is run via the command line. Here's an example of how you might do this:

```
java -jar /build/libs/ss-final.jar <arguments>
```

Here, `ss-final.jar` is the JAR file that gets generated after building the project, and `<arguments>` are the input parameters to the program. The parameters are optional and have default values in case they are not provided.

Here are the parameters detailed:

- `-dt <value>`: Time delta in seconds.
- `-dt2 <value>`: Save time delta in seconds.
- `-ct <value>`: Simulation cutoff time in seconds.
- `-o <value>`: Output file name.
- `-pFile <value>`: File path to load particles from it.
- `-pGen <value>`: True to save generated particles.
- `-pStableTime <value>`: Time to cut for particles stabilization.
- `-pStableEnergy <value>`: Kinetic energy threshold to cut for particles stabilization.
- `-n <value>`: Number of particles.
- `-pMass <value>`: Mass of the lowest radius particle.
- `-pld <value>`: Lower bound of particle's diameter.
- `-pud <value>`: Upper bound of particle's diameter.
- `-pKn <value>`: Particles Kn variable.
- `-pKt <value>`: Particles Kt variable.
- `-pGamma <value>`: Particles gamma variable.
- `-wallKn <value>`: Walls Kn variable.
- `-wallKt <value>`: Walls Kt variable.
- `-wallGamma <value>`: Particles wall variable.
- `-ballMass <value>`: Mass of the lowest radius particle.
- `-ballKn <value>`: Cannonball Kn variable.
- `-ballKt <value>`: Cannonball Kt variable.
- `-ballGamma <value>`: Cannonball gamma variable.
- `-ballAngle <value>`: Cannonball angle variable.
- `-ballVelocity <value>`: Cannonball velocity variable.
- `-ballDiameter <value>`: Cannonball diameter variable.
- `-ballHeight <value>`: Cannonball height variable.
- `-bw <value>`: Box width in meters.
- `-bh <value>`: Box height in meters.
- `-g <value>`: Gravity in m/s^2.
- `-logs <value>`: True if logs activated.

### Example Usage

```
java -jar /build/libs/ss-final.jar -dt 0.0001 -n 1500 
```

## How to Run the post-processing

To generate the necessary files execute:
```
python post-processing/executor.py
```

## Authors

Tomás Dallas

Tomás Dorado