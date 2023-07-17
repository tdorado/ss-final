from kinetic_energy_avg import plot_kinetic_energy_avg, get_kinetic_energy_avg, plot_all_kinetic_energies
from particle_velocity_avg import plot_particle_velocity_avg, get_particle_velocity_avg
from read_xyz import read_xyz_repetitions


def plot(min_gamma, max_gamma, gamma_step, number_repetitions):
    all_kinetic_energies = {}
    all_velocities = {}
    for g in range(min_gamma, max_gamma + 1, gamma_step):
        print(f"Procesando gamma = {g}")
        input_filename = f"./out/runs/g_{g}"
        data, times = read_xyz_repetitions(input_filename, number_repetitions)
        velocity_title = f"Velocidad de bala con gamma: {g}"
        velocity_stats = get_particle_velocity_avg(data, particle_id=0.0, time_interval=0.01)
        all_velocities[f"gamma = {g}"] = velocity_stats
        plot_particle_velocity_avg(velocity_stats, velocity_title)
        kinetic_title = f"Energía cinética con gamma: {g}"
        kinetic_stats = get_kinetic_energy_avg(data, time_interval=0.01)
        all_kinetic_energies[f"gamma = {g}"] = kinetic_stats
        plot_kinetic_energy_avg(kinetic_stats, kinetic_title)
    plot_all_kinetic_energies(all_kinetic_energies)

if __name__ == "__main__":
    repetitions = 5
    low_gamma = 300
    upper_gamma = 300
    gamma_steps = 25
    plot(low_gamma, upper_gamma, gamma_steps, repetitions)
