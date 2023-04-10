package legend.game.combat.effects;

import legend.core.memory.Value;
import legend.core.memory.types.IntRef;
import legend.core.memory.types.MemoryRef;
import legend.core.memory.types.UnsignedIntRef;

public class DeathDimensionEffect1c implements BttlScriptData6cSubBase1, MemoryRef {
  private final Value ref;

  public final UnsignedIntRef ptr_00;
  public final IntRef _04;
  public final IntRef _08;
  public final IntRef _0c;
  public final IntRef _10;
  public final IntRef _14;

  public DeathDimensionEffect1c(final Value ref) {
    this.ref = ref;

    this.ptr_00 = ref.offset(4, 0x00L).cast(UnsignedIntRef::new);
    this._04 = ref.offset(4, 0x04L).cast(IntRef::new);
    this._08 = ref.offset(4, 0x08L).cast(IntRef::new);
    this._0c = ref.offset(4, 0x0cL).cast(IntRef::new);
    this._10 = ref.offset(4, 0x10L).cast(IntRef::new);
    this._14 = ref.offset(4, 0x14L).cast(IntRef::new);
  }

  @Override
  public long getAddress() {
    return this.ref.getAddress();
  }
}
