import plotly.graph_objects as go
import numpy as np

def create_velocity_bullet_plot(file):
    time = []
    velocity_bullet = []

    with open(file, "r") as f:
        for line in f:
            data = line.split()
            particle_id = int(data[0])
            bullet_velocity = float(data[5])
            current_time = float(data[10])
            if particle_id == 0:
                time.append(current_time)
                velocity_bullet.append(bullet_velocity)

    fig_velocity_bullet = go.Figure()
    fig_velocity_bullet.add_trace(go.Scatter(x=time, y=velocity_bullet, mode='lines'))
    fig_velocity_bullet.update_layout(
        title="Evolución de la Velocidad de la Bala",
        xaxis_title="Tiempo [segundos]",
        yaxis_title="Velocidad ['m/s']",
        font=dict(size=16)
    )
    fig_velocity_bullet.show()

def create_system_kinetic_energy_plot(file):
    time = []
    kinetic_energy = []

    with open(file, "r") as f:
        for line in f:
            data = line.split()
            particle_id = int(data[0])
            velocity = float(data[5])
            current_time = float(data[10])
            if particle_id != 0:
                time.append(current_time)
                kinetic_energy.append(0.5 * velocity ** 2)

    fig_kinetic_energy = go.Figure()
    fig_kinetic_energy.add_trace(go.Scatter(x=time, y=kinetic_energy, mode='lines'))
    fig_kinetic_energy.update_layout(
        title="Energía Cinética del Sistema",
        xaxis_title="Tiempo [segundos]",
        yaxis_title="Energía Cinética [Joules]",
        font=dict(size=16)
    )
    fig_kinetic_energy.show()


