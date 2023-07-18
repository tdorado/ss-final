from kinetic_energy_avg import plot_kinetic_energy_avg, get_kinetic_energy_avg, plot_all_kinetic_energies
from particle_velocity_avg import plot_particle_velocity_avg, get_particle_velocity_avg, plot_all_velocities
from read_xyz import read_xyz_repetitions, read_xyz
from stabilization_time_avg import plot_avg_stabilization_times


def plot(variations, number_repetitions):
    all_kinetic_energies = {}
    all_velocities = {}
    for variation in variations:
        print(f"Procesando n particles = {variation}")
        input_filename = f"./out/runs/nParticles_{variation}"
        data, times = read_xyz_repetitions(input_filename, number_repetitions)
        velocity_title = f"Velocidad de bala con cantidad: {variation}"
        velocity_stats = get_particle_velocity_avg(data, particle_id=0.0, time_interval=0.01)
        all_velocities[f"partícula = {variation}"] = velocity_stats
        plot_particle_velocity_avg(velocity_stats, velocity_title)
        kinetic_title = f"Energía cinética con partículas: {variation}"
        kinetic_stats = get_kinetic_energy_avg(data, time_interval=0.01)
        all_kinetic_energies[f"partículas = {variation}"] = kinetic_stats
        plot_kinetic_energy_avg(kinetic_stats, kinetic_title)
    plot_all_kinetic_energies(all_kinetic_energies)
    plot_all_velocities(all_kinetic_energies)


def stabilization_times(variations, repetitions):
    all_times = {}
    for variation in variations:
        variation_times = []
        for rep in range(repetitions):
            print(f"Procesando n particles = {variation} rep = {rep}")
            input_filename = f"./out/runs/nParticles_{variation}_rep_{rep}"
            data, times = read_xyz(input_filename)
            variation_times.append(max(times))
        all_times[f"partículas = {variation}"] = variation_times
    plot_avg_stabilization_times(all_times, "Cantidad de partículas", "Tiempo promedio de estabilización")


if __name__ == "__main__":
    repetitions = 5
    variations = [2000, 3000, 4500]
    stabilization_times(variations, repetitions)
    plot(variations, repetitions)
