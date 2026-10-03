package com.volleyball.dvstat.service;

import com.volleyball.dvstat.model.MatchInfo;
import com.volleyball.dvstat.model.PlayerInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class DvMatchParser {

    public MatchInfo parse(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, Charset.defaultCharset());
        MatchInfo match = new MatchInfo();
        match.setSourceFile(file.toAbsolutePath().toString());
        parseSections(lines, match);
        return match;
    }

    private void parseSections(List<String> lines, MatchInfo match) {
        String section = "";

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty()) continue;

            if (line.startsWith("[") && line.endsWith("]")) {
                section = line;
                continue;
            }

            switch (section) {
                case "[3MATCH]":
                    parseMatchLine(line, match);
                    break;
                case "[3TEAMS]":
                    parseTeamLine(line, match);
                    break;
                case "[3MORE]":
                    parseMoreLine(line, match);
                    break;
                case "[3SET]":
                    match.getSetScores().add(line);
                    break;
                case "[3PLAYERS-H]":
                    parsePlayerLine(line, match, "HOME");
                    break;
                case "[3PLAYERS-V]":
                    parsePlayerLine(line, match, "AWAY");
                    break;
                default:
                    break;
            }
        }
    }

    private void parseMatchLine(String line, MatchInfo match) {
        String[] values = split(line);

        if (values.length > 0 && !value(values, 0).isEmpty() && isEmpty(match.getDate())) match.setDate(value(values, 0));
        if (values.length > 1 && !value(values, 1).isEmpty() && isEmpty(match.getTime())) match.setTime(value(values, 1));
        if (values.length > 2 && !value(values, 2).isEmpty() && isEmpty(match.getSeason())) match.setSeason(value(values, 2));
        if (values.length > 3 && !value(values, 3).isEmpty() && isEmpty(match.getCompetition())) match.setCompetition(value(values, 3));
        if (values.length > 4 && !value(values, 4).isEmpty() && isEmpty(match.getRound())) match.setRound(value(values, 4));
    }

    private void parseTeamLine(String line, MatchInfo match) {
        String[] values = split(line);

        if (values.length < 2) return;

        if (isEmpty(match.getHomeTeamName())) {
            match.setHomeTeamId(value(values, 0));
            match.setHomeTeamName(value(values, 1));
        } else if (isEmpty(match.getAwayTeamName())) {
            match.setAwayTeamId(value(values, 0));
            match.setAwayTeamName(value(values, 1));
        }
    }

    private void parseMoreLine(String line, MatchInfo match) {
        String[] values = split(line);

        if (values.length > 3 && !value(values, 3).isEmpty()) match.setCity(value(values, 3));
        if (values.length > 4 && !value(values, 4).isEmpty()) match.setVenue(value(values, 4));
    }

    private void parsePlayerLine(String line, MatchInfo match, String team) {
        String[] values = split(line);

        if (values.length < 11) return;

        PlayerInfo player = new PlayerInfo();
        player.setTeam(team);
        player.setSide(team);
        player.setJerseyNumber(parseInt(values, 1));
        player.setCode(value(values, 8));
        player.setLastName(value(values, 9));
        player.setFirstName(value(values, 10));

        if (values.length > 13) player.setPosition(value(values, 13));
        if (values.length > 14) player.setStarter(parseBoolean(values[14]));
        if (values.length > 15 && !value(values, 15).isEmpty()) player.setPlayerId(value(values, 15));
        else player.setPlayerId(player.getCode());

        match.getPlayers().add(player);
    }

    private String[] split(String line) {
        return line.split(";", -1);
    }

    private String value(String[] values, int index) {
        if (index < 0 || index >= values.length) return "";
        return values[index].trim();
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int parseInt(String[] values, int index) {
        try {
            return Integer.parseInt(value(values, index));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }
}