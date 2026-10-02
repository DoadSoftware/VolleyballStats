package com.volleyball.dvstat.model;

import java.util.ArrayList;
import java.util.List;

public class MatchStatistics {

    private String sourceFile;
    private String setupFile;
    private List<String> setScores = new ArrayList<>();
    private TeamStatistics homeTeam;
    private TeamStatistics awayTeam;

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSetupFile() {
        return setupFile;
    }

    public void setSetupFile(String setupFile) {
        this.setupFile = setupFile;
    }

    public List<String> getSetScores() {
        return setScores;
    }

    public void setSetScores(List<String> setScores) {
        this.setScores = setScores;
    }

    public TeamStatistics getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(TeamStatistics homeTeam) {
        this.homeTeam = homeTeam;
    }

    public TeamStatistics getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(TeamStatistics awayTeam) {
        this.awayTeam = awayTeam;
    }
}