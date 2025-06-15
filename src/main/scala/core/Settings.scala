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

  /** Fire probability decrease as a ratio of current humidity */
  var BASE_HUMIDITY_FIRE_DECREASE: Double = 0.6

  /** Growth probability increase as a ratio of current humidity */
  var BASE_HUMIDITY_GROWTH_INCREASE: Double = 0.001

  /** Additional fire probability per neighbor on fire */
  var NB_FIRE_INFLUENCE: Double = 0.2

  /** Growth probability decrease per neighbor on fire */
  var NB_FIRE_GROWTH_DECREASE: Double = 0.6

  /** Humidity decrease per neighbor on fire */
  var NB_FIRE_HUMIDITY_DECREASE: Double = 0.01

  /** Humidity increase per neighbor as a ratio of the difference between the two cells */
  var NB_HUMIDITY_INFLUENCE: Double = 0.05

  /** Additional humidity increase per water neighbor */
  var NB_WATER_HUMIDITY_INCREASE: Double = 0.2

  /** Growth probability increase per alive neighbor */
  var NB_ALIVE_GROWTH_INCREASE: Double = 0.05

  /** Fire probability decrease when a tree grows */
  var GROWTH_FIRE_DECREASE: Double = 0.1

  /** Humidity increase when a tree grows */
  var GROWTH_HUMIDITY_INCREASE: Double = 0.01

  // +-----------------+
  // |  Miscellaneous  |
  // +-----------------+

  /** Size of a cell in pixel */
  val CELL_SIZE: Int = 4

  /** Whether to connect to the Python plotting socket */
  val SOCKET_ENABLED: Boolean = false

  /** Host of the Python plotting socket */
  val SOCKET_HOST: String = ""

  /** Port of the Python plotting socket */
  val SOCKET_PORT: Int = 63610
}
