package com.volleyball.dvstat.model;

import com.sun.jna.Structure;

@Structure.FieldOrder({"Tot", "Perc"})
public class TDVOutAtt extends Structure {
    public int Tot;
    public int Perc;
}
