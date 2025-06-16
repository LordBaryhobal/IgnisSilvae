<h1 style="text-align: center">Ignis Silvae</h1>
<p align="center">
    <img src="res/icon.svg" width="128" />
</p>
<h4 style="text-align: center">A grid-based wildfire agent simulation in Scala.</h4>


This project is part of the 204.2 Computational Physics and 205.1 Functional Programming courses of the ISC Bachelor programme at HEI Sion.

---

<!-- TOC -->
* [Demo](#demo)
* [Usage](#usage)
  * [Installation](#installation)
  * [Controls](#controls)
* [Model](#model)
* [Analysis](#analysis)
<!-- TOC -->

---

# Demo

<p align="center">
    <img src="assets/demo.gif" width="600" /><br>
    <a href="assets/demo.mp4">MP4 Video</a>
</p>

# Usage

## Installation

1. Simply clone this repository and open the project in IntelliJ
2. Run the `App` class

## Controls

When running the visualization, you can use the following controls:

- <kbd>Space</kbd>: Toggle fast mode (disabled by default)
- <kbd>1</kbd> or <kbd>S</kbd>: Show `State` layer
- <kbd>2</kbd> or <kbd>B</kbd>: Show `Burn Frequency` layer
- <kbd>3</kbd> or <kbd>F</kbd>: Show `Fire Probability` layer
- <kbd>4</kbd> or <kbd>G</kbd>: Show `Growth Probability` layer
- <kbd>5</kbd> or <kbd>H</kbd>: Show `Humidity` layer
- <kbd>6</kbd> or <kbd>A</kbd>: Show `Fire Age` layer
- <kbd>ESC</kbd>: Quit

# Model

Each cell of the automaton has a state (alive, on fire, dead or water, for lakes), and a humidity level.
This state and humidity update on each time step, for all cells, according to the following rules and formulae.

<p align="center">
    <img src="assets/model_1.png" width="600" />
    <p align="center">Fig. 1 - Finite state machine</p>
</p>

---

Humidity is propagated across neighbors, and is globally decreased at each time step.
This is done to counterbalance the infinite humidity sources formed by lakes.

<p align="center">
    <img src="assets/model_2.png" width="600" />
    <p align="center">Fig. 2 - Humidity evolution</p>
</p>

---

The probability of an alive cell catching fire is determined by the number of neighbors on fire and its own dryness.
A tree also has a small probability of spontaneously catching fire without any neighbor on fire.

<p align="center">
    <img src="assets/model_3.png" width="600" />
    <p align="center">Fig. 3 - Fire probability computation</p>
</p>

---

The probability of a dead cell growing back is determined by the number of alive neighbors and its own humidity.
A tree also has a small probability of spontaneously growing without any alive neighbor.

<p align="center">
    <img src="assets/model_4.png" width="600" />
    <p align="center">Fig. 4 - Growth probability computation</p>
</p>

# Analysis

When varying the $\varphi$ parameter, a phase transition can be observed by looking at the mean tree coverage.
While it starts in a stable state neart 100% tree coverage for a small $\varphi$ value, it then drops in a transition phase.
A second stable phase then takes place near 1% tree coverage for high value of $\varphi$.

<p align="center">
    <img src="assets/transition.png" width="600" />
    <p align="center">Fig. 5 - Phase transition of the mean tree coverage</p>
</p>