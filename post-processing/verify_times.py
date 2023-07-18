from read_xyz import read_xyz
from read_xyz import read_xyz_repetitions


file_0 = "./out/runs/g_200_rep_0"
# Llamada a la función
data_0, times_0 = read_xyz(file_0)
print("menor file 0")
print(max(times_0))

file_1 = "./out/runs/g_200_rep_1"
# Llamada a la función
data_1, times_1 = read_xyz(file_1)
print("menor file 1")
print(max(times_1))

file_2 = "./out/runs/g_200_rep_2"
# Llamada a la función
data_2, times_2 = read_xyz(file_2)
print("menor file 2")
print(max(times_2))

file_3 = "./out/runs/g_200_rep_3"
# Llamada a la función
data_3, times_3 = read_xyz(file_3)
print("menor file 3")
print(max(times_3))

file_4 = "./out/runs/g_200_rep_4"
# Llamada a la función
data_4, times_4 = read_xyz(file_4)
print("menor file 4")
print(max(times_4))

variation = "./out/runs/g_200"
# Llamada a la función
data, times = read_xyz_repetitions(variation, 5)
print("menor de todos")
print(max(times))