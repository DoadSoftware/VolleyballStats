package com.volleyball.dvstat.model;

import java.util.ArrayList;
import java.util.List;

public class TeamStatistics {

    private String side;
    private List<StatisticEntry> matchStatistics = new ArrayList<>();
    private List<StatisticEntry> setStatistics = new ArrayList<>();
    private List<PlayerStatistics> players = new ArrayList<>();

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public List<StatisticEntry> getMatchStatistics() {
        return matchStatistics;
    }

    public List<StatisticEntry> getSetStatistics() {
        return setStatistics;
    }

    public List<PlayerStatistics> getPlayers() {
        return players;
    }
}