package com.volleyball.dvstat.service;

import com.volleyball.dvstat.model.MatchInfo;
import com.volleyball.dvstat.model.MatchStatistics;
import com.volleyball.dvstat.model.PlayerInfo;
import com.volleyball.dvstat.model.PlayerStatistics;
import com.volleyball.dvstat.model.StatisticEntry;
import com.volleyball.dvstat.model.TeamStatistics;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class DvStatBatchService {

    private static final int TEAM_TOTAL_PLAYER = 100;
    private static final int ALL_SKILLS = 0;
    private static final int LAST_SKILL = 7;
    private static final int MATCH_SET = 0;
    private static final int FIRST_SET = 1;

    private final DvStatService dvStatService;
    private final DvMatchParser dvMatchParser;

    public DvStatBatchService(DvStatService dvStatService, DvMatchParser dvMatchParser) {
        this.dvStatService = dvStatService;
        this.dvMatchParser = dvMatchParser;
    }

    public MatchStatistics readMatch(String dllPath, String statisticsFolder, File dvwFile, MatchInfo matchInfo) throws Exception {
        MatchStatistics matchStatistics = new MatchStatistics();
        matchStatistics.setSourceFile(dvwFile.getAbsolutePath());

        MatchInfo liveMatchInfo = dvMatchParser.parse(dvwFile.toPath());
        matchStatistics.setSetScores(liveMatchInfo.getSetScores());

        DvStatLibrary library = DvStatLibrary.load(new File(dllPath).getAbsolutePath());
        int setCount = liveMatchInfo.getSetScores().size();

        matchStatistics.setHomeTeam(buildTeamStatistics(matchInfo, library, statisticsFolder, 0, "HOME", setCount));
        matchStatistics.setAwayTeam(buildTeamStatistics(matchInfo, library, statisticsFolder, 1, "AWAY", setCount));

        return matchStatistics;
    }

    private TeamStatistics buildTeamStatistics(MatchInfo matchInfo, DvStatLibrary library, String statisticsFolder, int team, String side, int setCount) {
        TeamStatistics teamStatistics = new TeamStatistics();
        String teamName = team == 0 ? matchInfo.getHomeTeamName() : matchInfo.getAwayTeamName();
        addTeamMatchStatistics(teamStatistics, library, statisticsFolder, team, side, teamName);
        addTeamSetStatistics(teamStatistics, library, statisticsFolder, team, side, teamName, setCount);
        addPlayerStatistics(teamStatistics, library, statisticsFolder, matchInfo.getPlayers(), team, side, setCount);
        return teamStatistics;
    }

    private void addTeamMatchStatistics(TeamStatistics teamStatistics, DvStatLibrary library, String statisticsFolder, int team, String side, String teamName) {
        for (int skill = ALL_SKILLS; skill <= LAST_SKILL; skill++) {
            addStatistics(teamStatistics.getMatchStatistics(), library, statisticsFolder, team, side, TEAM_TOTAL_PLAYER, teamName, MATCH_SET, "MATCH", skill, getSkillName(skill));
        }
    }

    private void addTeamSetStatistics(TeamStatistics teamStatistics, DvStatLibrary library, String statisticsFolder, int team, String side, String teamName, int setCount) {
        for (int setNumber = FIRST_SET; setNumber <= setCount; setNumber++) {
            for (int skill = ALL_SKILLS; skill <= LAST_SKILL; skill++) {
                addStatistics(teamStatistics.getSetStatistics(), library, statisticsFolder, team, side, TEAM_TOTAL_PLAYER, teamName, setNumber, "SET", skill, getSkillName(skill));
            }
        }
    }

    private void addPlayerStatistics(TeamStatistics teamStatistics, DvStatLibrary library, String statisticsFolder, List<PlayerInfo> players, int team, String side, int setCount) {
        for (PlayerInfo player : players) {
            if (!side.equals(player.getTeam())) continue;

            PlayerStatistics playerStatistics = new PlayerStatistics();
            playerStatistics.setPlayerId(player.getPlayerId());
            playerStatistics.setJerseyNumber(player.getJerseyNumber());

            addPlayerMatchStatistics(playerStatistics, library, statisticsFolder, team, side, player);
            addPlayerSetStatistics(playerStatistics, library, statisticsFolder, team, side, player, setCount);

            teamStatistics.getPlayers().add(playerStatistics);
        }
    }

    private void addPlayerMatchStatistics(PlayerStatistics playerStatistics, DvStatLibrary library, String statisticsFolder, int team, String side, PlayerInfo player) {
        for (int skill = ALL_SKILLS; skill <= LAST_SKILL; skill++) {
            addStatistics(playerStatistics.getMatchStatistics(), library, statisticsFolder, team, side, player.getJerseyNumber(), player.getFullName(), MATCH_SET, "MATCH", skill, getSkillName(skill));
        }
    }

    private void addPlayerSetStatistics(PlayerStatistics playerStatistics, DvStatLibrary library, String statisticsFolder, int team, String side, PlayerInfo player, int setCount) {
    	for (int setNumber = FIRST_SET; setNumber <= setCount; setNumber++) {
            for (int skill = ALL_SKILLS; skill <= LAST_SKILL; skill++) {
                addStatistics(playerStatistics.getSetStatistics(), library, statisticsFolder, team, side, player.getJerseyNumber(), player.getFullName(), setNumber, "SET", skill, getSkillName(skill));
            }
        }
    }

    private void addStatistics(List<StatisticEntry> statistics, DvStatLibrary library, String statisticsFolder, 
    	int team, String side, int player, String playerName, int setNumber, String scope, int skill, String skillName) 
    {
        VolleyballStats values = dvStatService.readStatistics(library, statisticsFolder, team, player, skill, setNumber);
        StatisticEntry entry = new StatisticEntry();
        entry.setTeam(team);
        entry.setSide(side);
        entry.setPlayer(player);
        entry.setPlayerName(playerName);
        entry.setSkill(skill);
        entry.setSkillName(skillName);
        entry.setSetNumber(setNumber);
        entry.setScope(scope);
        entry.setStatistics(values);
        statistics.add(entry);
    }

    private String getSkillName(int skill) {
        switch (skill) {
            case 1:
                return "Serve";
            case 2:
                return "Reception";
            case 3:
                return "Attack";
            case 4:
                return "Block";
            case 5:
                return "Dig";
            case 6:
                return "Set";
            case 7:
                return "Free Ball";
            default:
                return "All Skills";
        }
    }
}