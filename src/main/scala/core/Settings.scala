package core

class Settings {
  // +--------------+
  // |  Generation  |
  // +--------------+

  /** World width */
  var WORLD_WIDTH: Int = 100

  /** World height */
  var WORLD_HEIGHT: Int = 100

  /** OpenSimplex noise feature size */
  var SIMPLEX_FEATURE_SIZE: Double = 18.0

  /** Maximum noise value for water to generate */
  var WATER_THRESHOLD: Double = 0.2

  /** Probability for each generated cell to start on fire */
  var INITIAL_FIRE_PROBABILITY: Double = 0.0005

  // +--------------+
  // |  Simulation  |
  // +--------------+

  /** Global humidity decrease rate at each time step */
  var GLOBAL_HUMIDITY_DECREASE_RATE: Double = 0.001

  /** Rate of humidity propagation, aka. neighbor influence */
  var HUMIDITY_PROPAGATION_RATE: Double = 0.4

  /** Humidity decrease when a cell catches fire */
  var BURN_HUMIDITY_DECREASE: Double = 0.1

  /** Humidity increase when a cell grows */
  var GROWTH_HUMIDITY_BOOST: Double = 0.1

  /** Additional "neighbor on fire" for spontaneous fires */
  var FIRE_PROBABILITY_OFFSET: Double = 0.0001  // Spontaneous fires

  /** Fire probability coefficient, aka. fire susceptibility */
  var FIRE_PROBABILITY_RATIO: Double = 0.3  // Fire propagation

  /** Additional "alive neighbor" for spontaneous growth */
  var GROWTH_PROBABILITY_OFFSET: Double = 0.001  // Spontaneous growth

  /** Growth probability coefficient, aka. growth susceptibility */
  var GROWTH_PROBABILITY_RATIO: Double = 0.01  // Growth propagation

  // +-----------------+
  // |  Miscellaneous  |
  // +-----------------+

  /** Size of a cell in pixel */
  val CELL_SIZE: Int = 10

  /** Whether to connect to the Python plotting socket */
  val SOCKET_ENABLED: Boolean = false

  /** Host of the Python plotting socket */
  val SOCKET_HOST: String = ""

  /** Port of the Python plotting socket */
  val SOCKET_PORT: Int = 63610
}
