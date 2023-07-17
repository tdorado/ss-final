import pandas as pd


def read_xyz(filename):
    filename_xyz = f'{filename}.xyz'
    try:
        with open(filename_xyz, 'r') as file:
            lines = file.readlines()
    except FileNotFoundError:
        print(f"El archivo {filename_xyz} no fue encontrado.")
        return None, None

    data_df = []
    times_df = []
    headers = ["id", "xPosition", "yPosition", "zPosition", "xVelocity", "yVelocity", "zVelocity", "radius", "mass",
               "pressure", "time"]
    i = 0
    while i < len(lines):
        num_particles = int(lines[i].strip())
        i += 2  # Saltamos el header
        for j in range(num_particles):
            particle_data = lines[i + j].strip().split()
            data_df.append({headers[k]: float(particle_data[k]) for k in range(len(headers))})
        times_df.append(float(particle_data[-1]))
        i += num_particles
    return data_df, times_df


def read_xyz_repetitions(filename_variation, num_repetitions):
    all_data = pd.DataFrame()
    min_time = float('inf')  # Iniciamos el menor tiempo con un valor muy alto

    for rep in range(0, num_repetitions):
        filename_rep = f'{filename_variation}_rep_{rep}'
        data_rep, times_rep = read_xyz(filename_rep)
        # Convertimos los datos en un DataFrame de pandas y agregamos una columna para la repetición
        data_df = pd.DataFrame(data_rep)
        data_df['repetition'] = rep
        all_data = pd.concat([all_data, data_df])
        # Actualizamos el menor tiempo, si es necesario
        max_time_rep = max(times_rep)
        if max_time_rep < min_time:
            min_time = max_time_rep

    # Filtramos los datos para incluir sólo hasta el menor tiempo
    all_data = all_data[all_data['time'] <= min_time]

    # Ordenamos los datos por tiempo y repetición
    all_data.sort_values(by=['time', 'repetition'], inplace=True)

    # Creamos la lista de times_frames
    times_frames = all_data['time'].unique().tolist()

    return all_data, times_frames
