import matplotlib.pyplot as plt


def plot(i, p, ax):
    with open(f"stats/fire_probability_ratio/{i}.csv", "r") as f:
        lines = f.readlines()
        x = []
        y = []
        for line in lines[1:]:
            line = line.strip()
            if line == "":
                continue
            values = line.split(",")
            x.append(int(values[0]))
            y.append(float(values[2]))

        ax.plot(x, p, y, label=f"$\\varphi = {p}$")


if __name__ == "__main__":
    fig = plt.figure("Phase transition", figsize=(12, 10))
    ax = fig.add_subplot(projection='3d')
    ax.set_title("Tree coverage evolution with $\\varphi \\in [0, 1]$")
    ax.set(
        xlabel="$t$",
        ylabel="$\\varphi$",
        zlabel="$\\overline{\\rho_{alive}}$",
    )

    v_min = 0
    v_max = 1
    n = 64
    for i in range(n)[::-1]:
        v = v_min + (v_max - v_min) * i / (n - 1)
        plot(i, v, ax)
    
    #plt.legend()
    plt.show()
