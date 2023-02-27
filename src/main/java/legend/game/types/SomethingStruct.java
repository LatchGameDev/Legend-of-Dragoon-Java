package legend.game.types;

import legend.core.gte.GsCOORDINATE2;
import legend.core.gte.GsDOBJ2;
import legend.core.gte.SVECTOR;
import legend.core.gte.TmdObjTable1c;
import legend.core.gte.TmdWithId;
import legend.core.memory.types.UnboundedArrayRef;

public class SomethingStruct {
  public TmdObjTable1c[] objTableArrPtr_00;
  public SVECTOR[] verts_04;
  public SVECTOR[] normals_08;
  public int count_0c;
  public TmdObjTable1c.Primitive[] primitives_10;
  /** 0xc bytes each */
  public UnboundedArrayRef<SomethingStructSub0c_1> ptr_14;
  public UnboundedArrayRef<SomethingStructSub0c_2> ptr_18;
  public TmdWithId tmdPtr_1c;
  public GsDOBJ2 dobj2Ptr_20;
  public GsCOORDINATE2 coord2Ptr_24;

  public TmdObjTable1c.Primitive getPrimitiveForOffset(final int offset) {
    int primitivesIndex;
    for(primitivesIndex = 0; primitivesIndex < this.primitives_10.length - 1; primitivesIndex++) {
      if(this.primitives_10[primitivesIndex + 1].offset() > offset) {
        break;
      }
    }

    return this.primitives_10[primitivesIndex];
  }
}
