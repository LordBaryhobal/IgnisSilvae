import matplotlib
import matplotlib.pyplot as plt
import socket
import struct

HOST = ""
PORT = 63610

def main():
    #matplotlib.use("Qt5agg")
    plt.ion()
    fig = plt.figure()
    plt.ylim((0, 1))

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        print(f"Listening on port {PORT}")

        while True:
            c = input("> ")
            if c:
                break

            s.listen(1)
            conn, addr = s.accept()

            indices = []
            densities = []

            with conn:
                print("Connected by", addr)
                while True:
                    data = conn.recv(12)
                    #print(f"Received {len(data)} bytes")
                    if len(data) == 12:
                        i, fire_density = struct.unpack(">Id", data)
                        indices.append(i)
                        densities.append(fire_density)
                        
                        try:
                            plt.clf()
                            plt.plot(indices, densities)
                            plt.show()
                            #plt.pause(0.01)
                            fig.canvas.start_event_loop(0.01)
                        except Exception as e:
                            pass
                    else:
                        break
            
            print("Closed connection")

if __name__ == "__main__":
    main()
