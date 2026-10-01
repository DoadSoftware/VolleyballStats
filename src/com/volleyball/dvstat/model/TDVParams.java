package com.volleyball.dvstat.model;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({"PathSource", "IdTeam", "IdPlayer", "Skill", "SetN", "IDCall"})
public class TDVParams extends Structure {
    public Pointer PathSource;
    public int IdTeam;
    public int IdPlayer;
    public int Skill;
    public int SetN;
    public int IDCall;
}
