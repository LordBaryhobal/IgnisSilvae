## Global properties
- Humidity offset (rain)
- Temperature

## Cell properties
- State:
  - Alive
  - Burning
  - Dead
  - Water
- Humidity
- Wind
  - Direction
  - (Strength)
- (Height)

## Computation
- Fire probability
  - Temperature: + => +
  - Humidity: + => -
  - Wind:
    - From fire: + => +
    - Against fire: + => -
- Growth probability
  - Humidity: + => +
  - Wind: - => +
- Humidity:
  - Alive: => +
  - Wind:
    - From water: + => +
    - From fire: + => -

## Phase
- Fire density
- Fire speed