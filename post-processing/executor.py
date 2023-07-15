import tqdm


REPETITIONS = 5

def execute_variation(variation_string, variation_name):
    for i in tqdm(range(REPETITIONS)):
        print(cmd)
        cmd = f"java -jar ../build/libs/ss-final-1.0.jar -o {variation_name}_rep_{i} {variation_string}"
        os.system(cmd)

def execute_dimeters_variation():
    params = [[0.008, 0.015, "d1"], [0.015, 0.03, "d2"], [0.04 , 0.06, "d3"]]
    for diameters_range in params:
        execute_variation(f"-pld {diameters_range[0]} -pud {diameters_range[1]}", diameters_range[2])

def execute_gammas_variation():
    params = [[10.0, "g_10"], [20.0, "g_20"], [30.0, "g_30"],[40.0, "g_40"],[50.0, "g_50"],[60.0, "g_60"],[70.0, "g_70"],[80.0, "g_80"],[90.0, "g_90"],[100.0, "g_100"]]
    for gamma in params:
        execute_variation(f"-pGamma {gamma[0]}",  gamma[1])

def execute_nParticles_variation():
    params = [[1500, "nParticles_1500"], [3000, "nParticles_3000"], [4500, "nParticles_4500"]]
     for nParticle in params:
        execute_variation(f"-n {nParticle[0]}",  nParticle[1])

def execute_angles_variation():
    angles = [[35.0, "bAngle_35"], [40.0, "bAngle_40"], [45.0, "bAngle_45"], [50, "bAngle_50"], [55, "bAngle_55"]]
     for angle in params:
        execute_variation(f"-ballAngle {angle[0]}",  angle[1])

