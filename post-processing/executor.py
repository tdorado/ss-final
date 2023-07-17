from tqdm import tqdm
import os


REPETITIONS = 5

def execute_variation(variation_string, variation_name):
    for i in tqdm(range(REPETITIONS)):
        print("Started simulating variation: " + variation_name + "...")
        cmd = f"java -jar build/libs/ss-final-1.0.jar -o {variation_name}_rep_{i} {variation_string}"
        os.system(cmd)

def execute_dimeters_variation():
    params = [[0.008, 0.015, "d1"], [0.015, 0.03, "d2"], [0.04 , 0.06, "d3"]]
    for diameters_range in params:
        execute_variation(f"-pld {diameters_range[0]} -pud {diameters_range[1]}", diameters_range[2])

def execute_gammas_variation():
    params = [ [50.0, "g_50"], [75.0, "g_75"], [100.0, "g_100"],[125.0, "g_125"],[150.0, "g_150"],[175.0, "g_175"],[200.0, "g_200"],[225.0, "g_225"],[250.0, "g_250"]]
    for gamma in params:
        execute_variation(f"-pGamma {gamma[0]}",  gamma[1])

def execute_nParticles_variation():
    params = [[1500, "nParticles_1500"], [3000, "nParticles_3000"], [4500, "nParticles_4500"]]
    for nParticle in params:
        execute_variation(f"-n {nParticle[0]}",  nParticle[1])

def execute_angles_variation():
    params = [[35.0, "bAngle_35"], [40.0, "bAngle_40"], [45.0, "bAngle_45"], [50, "bAngle_50"], [55, "bAngle_55"]]
    for angle in params:
        execute_variation(f"-ballAngle {angle[0]}",  angle[1])

execute_gammas_variation()