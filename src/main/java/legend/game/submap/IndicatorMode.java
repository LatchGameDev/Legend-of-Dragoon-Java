package legend.game.submap;

/** Controls how the indicators (triangles) are drawn on submaps (called "Note" in retail option menu) */
public enum IndicatorMode {
  OFF("Off"),
  MOMENTARY("Momentary"),
  ON("On"),
  ;

  public final String name;

  IndicatorMode(final String name) {
    this.name = name;
  }
}
