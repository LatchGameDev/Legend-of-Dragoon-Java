package legend.game.combat.deff;

import legend.game.unpacker.FileData;

public abstract class Anim {
  public int magic_00;

  public Anim(final FileData data) {
    this.magic_00 = data.readInt(0);
  }
}
