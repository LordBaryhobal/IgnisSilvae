package core

object Settings {
  // +--------------+
  // |  Generation  |
  // +--------------+

  /** World width */
  val WORLD_WIDTH: Int = 100

  /** World height */
  val WORLD_HEIGHT: Int = 100

  /** OpenSimplex noise feature size */
  val SIMPLEX_FEATURE_SIZE: Double = 24.0

  /** Maximum noise value for water to generate */
  val WATER_THRESHOLD: Double = 0.2

  // +--------------+
  // |  Simulation  |
  // +--------------+

  /** Fire probability decrease as a ratio of current humidity */
  val BASE_HUMIDITY_FIRE_DECREASE: Double = 0.5

  /** Growth probability increase as a ratio of current humidity */
  val BASE_HUMIDITY_GROWTH_INCREASE: Double = 0.1

  /** Additional fire probability per neighbor on fire */
  val NB_FIRE_INFLUENCE: Double = 0.2

  /** Growth probability decrease per neighbor on fire */
  val NB_FIRE_GROWTH_DECREASE: Double = 0.05

  /** Humidity increase per neighbor as a ratio of the difference between the two cells */
  val NB_HUMIDITY_INFLUENCE: Double = 0.05

  /** Additional humidity increase per water neighbor */
  val NB_WATER_HUMIDITY_INCREASE: Double = 0.2

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
