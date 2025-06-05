package core

object Settings {
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
}
