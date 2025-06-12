import matplotlib
import matplotlib.pyplot as plt
import socket
import struct

import numpy as np

HOST = ""
PORT = 63610

def rolling_mean(a: np.ndarray, win_size: int):
    n: int = win_size
    ret = np.cumsum(a, dtype=float)
    ret[n:] = ret[n:] - ret[:-n]
    return np.hstack([np.zeros(n - 1), ret[n - 1:] / n])

def main():
    # Use a non-blocking backend for live plotting
    matplotlib.use('TkAgg')  # or 'Qt5Agg' if you have PyQt5
    plt.ion()
    
    fig, ax = plt.subplots()
    ax2 = ax.twinx()
    
    # Set up the plots
    #ax.set_ylim((0, 1))
    ax.set_xlabel("Step")
    ax.set_ylabel("Ratio of total area on fire")
    ax2.set_ylabel("Mean age")
    
    # Initialize empty line objects
    line1, = ax.plot([], [], '-b', label="Fire density")
    line2, = ax.plot([], [], '-g', label="Tree density")
    line3, = ax2.plot([], [], '-r', label="Mean fire age")
    line4, = ax2.plot([], [], '-c', label="Mean fire age (rolling mean)")
    
    # Set up legend
    lines = [line1, line2, line3, line4]
    labels = [l.get_label() for l in lines]
    ax.legend(lines, labels, loc='upper left')
    
    plt.show(block=False)
    plt.draw()

    n_bytes = 4 + 8 + 8 + 8

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        print(f"Listening on port {PORT}")

        while True:
            s.listen(1)
            conn, addr = s.accept()

            indices = []
            fire_densities = []
            tree_densities = []
            ages = []

            with conn:
                print("Connected by", addr)
                while True:
                    data = conn.recv(n_bytes)
                    if len(data) == n_bytes:
                        i, fire_density, tree_density, mean_age = struct.unpack(">Iddd", data)
                        indices.append(i)
                        fire_densities.append(fire_density)
                        tree_densities.append(tree_density)
                        ages.append(mean_age)
                        
                        try:
                            # Redraw the plot
                            if len(indices) % 10 == 0:
                                # Update the line data instead of clearing everything
                                line1.set_data(indices, fire_densities)
                                line2.set_data(indices, tree_densities)
                                line3.set_data(indices, ages)
                                
                                # Adjust x-axis limits to fit new data
                                if indices:
                                    ax.set_xlim(min(indices), max(indices))
                                    ax2.set_xlim(min(indices), max(indices))
                                
                                # Adjust y-axis limits if needed
                                if fire_densities and tree_densities:
                                    ax.set_ylim(0, max(fire_densities + tree_densities) * 1.1)
                                if ages:
                                    ax2.set_ylim(0, max(ages) * 1.1)

                                average = rolling_mean(ages, 100)
                                if average.shape[0] == len(indices):
                                    line4.set_data(indices, average)
                            
                                fig.canvas.draw()
                                fig.canvas.flush_events()
                                plt.pause(0.001)  # Small pause to allow GUI to update
                            
                        except Exception as e:
                            print(f"Plotting error: {e}")
                    else:
                        break
            
            print("Closed connection")

if __name__ == "__main__":
    main()