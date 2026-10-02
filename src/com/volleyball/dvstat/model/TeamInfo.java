package com.volleyball.dvstat.model;

import java.util.ArrayList;
import java.util.List;

public class TeamInfo {

    private int team;
    private String side;
    private String id;
    private String code;
    private String name;
    private final List<PlayerInfo> players = new ArrayList<>();

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }
}