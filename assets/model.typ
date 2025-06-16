#import "@preview/showybox:2.0.4": showybox
#import "@preview/fletcher:0.5.8" as fletcher: diagram, edge, node

#set page(
  width: 15cm,
  height: auto,
  margin: .5cm,
  fill: luma(0%).transparentize(100%)
)

#set text(
  font: "Source Sans 3"
)

#let settings = (
  lambda: "GLOBAL_HUMIDITY_DECREASE_RATE",
  nu: "HUMIDITY_PROPAGATION_RATE",
  sigma: "BURN_HUMIDITY_DECREASE",
  eta: "GROWTH_HUMIDITY_BOOST",
  phi: "FIRE_PROBABILITY_RATIO",
  epsilon: "FIRE_PROBABILITY_OFFSET",
  tau: "GROWTH_PROBABILITY_RATIO",
  delta: "GROWTH_PROBABILITY_OFFSET"
)

#let setting(var) = {
  raw("Settings." + settings.at(var), lang: "scala")
}

#let equ-box(name, equation, bg: none, fg: black) = {
  showybox(
    frame: (
      title-color: bg
    ),
    title-style: (
      color: fg
    ),
    title: strong(name),
    equation
  )
}

#let states = (
  alive: green.lighten(30%),
  fire: orange.lighten(30%),
  dead: gray.lighten(30%),
  water: blue.lighten(50%)
)
#let state-bx(state) = {
  underline(
    stroke: (
      paint: states.at(lower(state)),
      thickness: 2pt
    ),
    state
  )
}

#equ-box(bg: gray.lighten(60%))[Automaton States][#align(
  center,
  diagram(
    node-shape: circle,
    node((0, 0), [Alive], fill: states.alive, radius: 1.5em),
    edge($p(f)$, "-|>"),
    node((3, 0), [Fire], fill: states.fire, radius: 1.5em),
    edge("-|>", bend: 20deg),
    node((1.5, 2), [Dead], fill: states.dead, radius: 1.5em),
    edge((0, 0), $p(g)$, "-|>", bend: 20deg),
    node((4, 1), [Water], fill: states.water, radius: 1.5em),
    edge((4, 1), "-|>", bend: 130deg)
  )
)]

#pagebreak()

#equ-box(bg: blue.lighten(20%), fg: white)[Humidity][
  $ H_(i+1) = H_i dot (1 - lambda) + ( 1 / N sum_"neighbors" H_n - H_i) dot nu
  + cases(
    gap: #0.6em,
    - & sigma quad & "if "#state-bx("Alive") -> #state-bx("Fire"),
      & eta quad & "if "#state-bx("Dead") -> #state-bx("Alive"),
      & 0 quad & "otherwise"
  )\
  H in [0, 1]
  $
  where $lambda = $ #setting("lambda")\
  and $nu = $ #setting("nu")\
  and $sigma = $ #setting("sigma")\
  and $eta = $ #setting("eta")
]

#pagebreak()

#equ-box(bg: states.fire)[Fire Probability][
  $ p(f) = phi dot ("#nb"_"FIRE" + epsilon) dot sqrt(1 - H) $
  where $phi = $ #setting("phi")\
  and $epsilon = $ #setting("epsilon")
]

#pagebreak()

#equ-box(bg: states.alive)[Growth Probability][
  $ p(g) = tau dot ("#nb"_"ALIVE" + delta) dot sqrt(H) $
  where $tau = $ #setting("tau")\
  and $delta = $ #setting("delta")
]
