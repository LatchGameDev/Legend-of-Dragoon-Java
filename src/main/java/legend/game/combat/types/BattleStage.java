package legend.game.combat.types;

import legend.core.gte.GsCOORD2PARAM;
import legend.core.gte.GsCOORDINATE2;
import legend.core.gte.GsDOBJ2;
import legend.core.gte.GsOBJTABLE2;
import legend.core.gte.Tmd;
import legend.game.types.CContainerSubfile2;
import legend.game.types.ModelPartTransforms0c;

import java.util.Arrays;

public class BattleStage {
  public final GsDOBJ2[] dobj2s_00 = new GsDOBJ2[10];
  public final GsCOORDINATE2[] coord2s_a0 = new GsCOORDINATE2[10];
  public final GsCOORD2PARAM[] params_3c0 = new GsCOORD2PARAM[10];
  public final GsOBJTABLE2 objTable2_550 = new GsOBJTABLE2();
  public final GsCOORDINATE2 coord2_558 = new GsCOORDINATE2();
  public final GsCOORD2PARAM param_5a8 = new GsCOORD2PARAM();
  public Tmd tmd_5d0;
  /** [keyframe][part] */
  public ModelPartTransforms0c[][] rotTrans_5d4;
  /** [keyframe][part] */
  public ModelPartTransforms0c[][] rotTrans_5d8;
  /** short */
  public int partCount_5dc;
  /** short */
  public int totalFrames_5de;
  /** short */
  public int animationState_5e0;
  /** short */
  public int remainingFrames_5e2;
  public int _5e4;
  /** short */
  public int z_5e8;

  public CContainerSubfile2 _5ec;
  public final short[][] _5f0 = new short[10][];
  /** ubyte */
  public final int[] _618 = new int[10];
  /** ushort */
  public final int[] _622 = new int[10];
  /** ushort */
//  public final int[] _636 = new int[10];
  /** short */
  public final int[] _64a = new int[10];
  /** short */
  public final int[] _65e = new int[10];

  public BattleStage() {
    Arrays.setAll(this.dobj2s_00, i -> new GsDOBJ2());
    Arrays.setAll(this.coord2s_a0, i -> new GsCOORDINATE2());
    Arrays.setAll(this.params_3c0, i -> new GsCOORD2PARAM());
  }
}
