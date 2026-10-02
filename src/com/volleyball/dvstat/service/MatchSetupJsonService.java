package com.volleyball.dvstat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.volleyball.dvstat.model.MatchInfo;
import com.volleyball.dvstat.model.MatchSetup;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class MatchSetupJsonService {

    private final ObjectMapper objectMapper;

    public MatchSetupJsonService() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Path writeSetupJson(MatchInfo matchInfo, Path sourceFile, Path outputDirectory) throws Exception {
        Files.createDirectories(outputDirectory);

        String fileName = buildFileName(matchInfo) + ".json";
        Path outputFile = outputDirectory.resolve(fileName);

        MatchSetup setup = new MatchSetup();
        setup.setSourceFile(sourceFile.toAbsolutePath().toString());
        setup.setMatchInfo(matchInfo);

        objectMapper.writeValue(outputFile.toFile(), setup);

        return outputFile;
    }

    public MatchSetup readSetupJson(Path setupFile) throws Exception {
        return objectMapper.readValue(setupFile.toFile(), MatchSetup.class);
    }

    public String buildFileName(MatchInfo matchInfo) {
        String date = formatDate(matchInfo.getDate());
        String time = formatTime(matchInfo.getTime());

        if (date.isEmpty()) date = "UNKNOWN_DATE";
        if (time.isEmpty()) time = "000000";

        String homeTeam = sanitizeFileName(matchInfo.getHomeTeamName());
        String awayTeam = sanitizeFileName(matchInfo.getAwayTeamName());

        if (homeTeam.isEmpty()) homeTeam = "UNKNOWN_HOME";
        if (awayTeam.isEmpty()) awayTeam = "UNKNOWN_AWAY";

        return date + "_" + time + "_" + homeTeam + "_vs_" + awayTeam;
    }

    private String formatDate(String value) {
        if (value == null || value.trim().isEmpty()) return "";

        String date = value.trim();

        try {
            java.time.LocalDate parsed = java.time.LocalDate.parse(date, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return parsed.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception ignored) {
        }

        try {
            java.time.LocalDate parsed = java.time.LocalDate.parse(date, java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            return parsed.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception ignored) {
        }

        return date.replaceAll("[^0-9]", "");
    }

    private String formatTime(String value) {
        if (value == null || value.trim().isEmpty()) return "";

        String time = value.trim();

        try {
            java.time.LocalTime parsed = java.time.LocalTime.parse(time, java.time.format.DateTimeFormatter.ofPattern("HH.mm.ss"));
            return parsed.format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
        } catch (Exception ignored) {
        }

        try {
            java.time.LocalTime parsed = java.time.LocalTime.parse(time, java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            return parsed.format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
        } catch (Exception ignored) {
        }

        return time.replaceAll("[^0-9]", "");
    }

    private String sanitizeFileName(String value) {
        if (value == null) return "";
        return value.trim().replaceAll("[\\\\/:*?\"<>|]", "-").replaceAll("\\s+", "-");
    }
}