package com.volleyball.dvstat.model;

import com.sun.jna.Structure;

@Structure.FieldOrder({"MM", "MMP", "MMC", "M", "S", "SP", "SC", "P", "D", "DP", "DC", "E", "Tot"})
public class TDVOutSymbolEx extends Structure {
    public int MM;
    public int MMP;
    public int MMC;
    public int M;
    public int S;
    public int SP;
    public int SC;
    public int P;
    public int D;
    public int DP;
    public int DC;
    public int E;
    public int Tot;
}
