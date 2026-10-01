package com.volleyball.dvstat.model;

import com.sun.jna.Structure;

@Structure.FieldOrder({"Pts", "Err", "Tot"})
public class TDVOutPtsErr extends Structure {
    public int Pts;
    public int Err;
    public int Tot;
}
