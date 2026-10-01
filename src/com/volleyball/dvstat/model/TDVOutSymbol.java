package com.volleyball.dvstat.model;

import com.sun.jna.Structure;

@Structure.FieldOrder({"MM", "M", "S", "P", "D", "E", "Tot"})
public class TDVOutSymbol extends Structure {
    public int MM;
    public int M;
    public int S;
    public int P;
    public int D;
    public int E;
    public int Tot;
}
