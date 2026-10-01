package com.volleyball.dvstat.model;

import com.sun.jna.Structure;

@Structure.FieldOrder({"Tot", "Pos"})
public class TDVOutRec extends Structure {
    public int Tot;
    public int Pos;
}
