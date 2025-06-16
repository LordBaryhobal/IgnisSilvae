import os
import matplotlib.pyplot as plt

import numpy as np

from scipy.optimize import curve_fit


def to_float(v: str) -> float:
    return 0 if v.lower() == "nan" else float(v)


def get_values(i):
    with open(f"stats/fire_probability_ratio/{i}.csv", "r") as f:
        lines = f.readlines()
        y = []
        for line in lines[1:]:
            line = line.strip()
            if line == "":
                continue
            step, fire_density, tree_density, fire_age = line.split(",")
            tree_density = to_float(tree_density)
            y.append(tree_density)
        
    return y


def sigmoid(x, L ,x0, k, b):
    y = L / (1 + np.exp(-k* (x - x0))) + b
    return y

def sigmoid_inv(y, L, x0, k, b):
    x = x0 - np.log(L / (y - b) - 1) / k
    return x


if __name__ == "__main__":
    fig = plt.figure("Phase transition", figsize=(10, 6))
    plt.title("Mean tree coverage with $\\varphi \\in [0, 1]$")
    plt.xlabel("$\\varphi$")
    plt.ylabel("$\\overline{\\rho_{alive}}$")

    x = []
    y = []
    v_min = 0
    v_max = 1
    n = 64
    for i in range(n):
        v = v_min + (v_max - v_min) * i / (n - 1)
        x.append(v)
        alive = get_values(i)
        y.append(np.mean(alive))
    
    
    # https://stackoverflow.com/a/62215374/11109181
    p0 = [np.max(y), np.median(x), 1, np.min(x)]
    popt = curve_fit(sigmoid, x, y, p0, method='dogbox')[0]
    
    fit_x = np.linspace(v_min, v_max, 100)
    fit_y = sigmoid(fit_x, *popt)
    plt.plot(fit_x, fit_y, color="r", label="Sigmoid fit", zorder=1)
    plt.scatter(x, y, label="Measured means", zorder=2)
    plt.axhline(0.5, color="gray", linestyle="--", linewidth=1)
    mid_x = sigmoid_inv(0.5, *popt)
    plt.axvline(mid_x, color="gray", linestyle="--", linewidth=1)
    plt.text(0, 0.53, "$\\overline{\\rho_{ALIVE}} = 0.5$", bbox=dict(facecolor="none", pad=2), va="bottom")
    
    plt.legend()
    plt.tight_layout()
    plt.savefig(os.path.join(
        os.path.dirname(__file__),
        os.pardir,
        os.pardir,
        os.pardir,
        "assets",
        "transition.png"
    ))
    plt.show()
