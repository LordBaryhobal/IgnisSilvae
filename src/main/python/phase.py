import os
import matplotlib.pyplot as plt

import numpy as np


def to_float(v: str) -> float:
    return 0 if v.lower() == "nan" else float(v)


def get_values(i):
    with open(f"stats/fire_probability_ratio/{i}.csv", "r") as f:
        lines = f.readlines()
        x = []
        y = []
        for line in lines[1:]:
            line = line.strip()
            if line == "":
                continue
            step, fire_density, tree_density, fire_age = line.split(",")
            fire_density = to_float(fire_density)
            tree_density = to_float(tree_density)
            fire_age = to_float(fire_age)
            x.append(tree_density)
            y.append(fire_density)
        
    return x, y


if __name__ == "__main__":
    fig = plt.figure("Phase diagram", figsize=(10, 8))
    plt.title("Tree vs. fire coverage with $\\varphi \\in [0, 1]$")
    plt.xlabel("$\\rho_{alive}$")
    plt.ylabel("$\\rho_{fire}$")

    x = []
    y = []
    z = []
    v_min = 0
    v_max = 1
    n = 64
    for i in range(n):
        v = v_min + (v_max - v_min) * i / (n - 1)
        z.append(v)
        alive, fire = get_values(i)
        x.append(np.mean(alive))
        y.append(np.mean(fire))
    
    p = plt.scatter(x, y, c=z)
    plt.colorbar(p, label="$\\varphi$")
    plt.tight_layout()
    plt.savefig(os.path.join(
        os.path.dirname(__file__),
        os.pardir,
        os.pardir,
        os.pardir,
        "assets",
        "phase.png"
    ))
    plt.show()
