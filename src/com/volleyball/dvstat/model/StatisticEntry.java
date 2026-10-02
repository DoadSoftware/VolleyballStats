package com.volleyball.dvstat.model;

import com.volleyball.dvstat.service.VolleyballStats;

public class StatisticEntry {

    private int team;
    private String side;
    private int player;
    private String playerName;
    private int skill;
    private String skillName;
    private int setNumber;
    private String scope;
    private VolleyballStats statistics;

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

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public VolleyballStats getStatistics() {
        return statistics;
    }

    public void setStatistics(VolleyballStats statistics) {
        this.statistics = statistics;
    }
}