package com.volleyball.dvstat.model;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatistics {

    private String playerId;
    private int jerseyNumber;
    private List<StatisticEntry> matchStatistics = new ArrayList<>();
    private List<StatisticEntry> setStatistics = new ArrayList<>();

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public List<StatisticEntry> getMatchStatistics() {
        return matchStatistics;
    }

    public List<StatisticEntry> getSetStatistics() {
        return setStatistics;
    }
}