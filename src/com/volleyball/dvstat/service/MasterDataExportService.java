package com.volleyball.dvstat.service;

import com.volleyball.dvstat.model.MatchInfo;
import com.volleyball.dvstat.model.PlayerInfo;

import io.github.spannm.jackcess.Database;
import io.github.spannm.jackcess.DatabaseBuilder;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MasterDataExportService {

	private static final String DATABASE_PATH = "C:\\Sports\\Volleyball\\Database\\Volleyball.MDB";	

    private final DvMatchParser dvMatchParser;

    public MasterDataExportService(DvMatchParser dvMatchParser) {
        this.dvMatchParser = dvMatchParser;
    }

    private void createDatabaseIfNeeded() throws Exception {
        File databaseFile = new File(DATABASE_PATH);

        if (!databaseFile.exists()) {
            try (Database database = DatabaseBuilder.create(Database.FileFormat.V2000, databaseFile)) {
            }
        }
    }    
    
    public ExportResult export(String statisticsFolder) throws Exception 
    {
    	Path databaseDirectory = Path.of(DATABASE_PATH).getParent();
    	Files.createDirectories(databaseDirectory);
    	createDatabaseIfNeeded();

        Map<String, TeamRecord> teams = new LinkedHashMap<>();
        Map<String, PlayerRecord> players = new LinkedHashMap<>();

        File folder = new File(statisticsFolder);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".dvw"));

        if (files == null) {
            return new ExportResult(0, 0, 0);
        }

        for (File file : files) {
            MatchInfo matchInfo = dvMatchParser.parse(file.toPath());
            addTeam(teams, matchInfo.getHomeTeamId(), matchInfo.getHomeTeamName(), "HOME");
            addTeam(teams, matchInfo.getAwayTeamId(), matchInfo.getAwayTeamName(), "AWAY");

            for (PlayerInfo player : matchInfo.getPlayers()) {
                if (player.getPlayerId() == null || player.getPlayerId().trim().isEmpty()) {
                    continue;
                }

                PlayerRecord record = new PlayerRecord();
                record.playerId = player.getPlayerId();
                record.teamId = "HOME".equalsIgnoreCase(player.getTeam()) ? matchInfo.getHomeTeamId() : matchInfo.getAwayTeamId();
                record.jerseyNumber = player.getJerseyNumber();
                record.code = player.getCode();
                record.lastName = player.getLastName();
                record.firstName = player.getFirstName();
                record.fullName = player.getFullName();
                record.position = player.getPosition();
                record.starter = player.isStarter();
                record.side = player.getSide();

                players.put(record.playerId, record);
            }
        }

        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

        try (Connection connection = DriverManager.getConnection("jdbc:ucanaccess://" + DATABASE_PATH)) {        
            createTables(connection);

            for (TeamRecord team : teams.values()) {
                upsertTeam(connection, team);
            }

            for (PlayerRecord player : players.values()) {
                upsertPlayer(connection, player);
            }
        }

        return new ExportResult(files.length, teams.size(), players.size());
    }

    private void addTeam(Map<String, TeamRecord> teams, String id, String name, String side) {
        if (id == null || id.trim().isEmpty()) {
            return;
        }

        TeamRecord team = teams.get(id);

        if (team == null) {
            team = new TeamRecord();
            team.teamId = id;
            team.teamCode = id;
            team.teamName = name;
            team.shortName = name;
            team.side = side;
            teams.put(id, team);
            return;
        }

        if (isEmpty(team.teamName) && !isEmpty(name)) {
            team.teamName = name;
        }
    }

    private void createTables(Connection connection) throws Exception {
        if (!tableExists(connection, "Teams")) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE Teams (TeamID VARCHAR(255) NOT NULL, TeamCode VARCHAR(255), TeamName VARCHAR(255), ShortName VARCHAR(255), Side VARCHAR(20), CONSTRAINT PK_Teams PRIMARY KEY (TeamID))");
            }
        }

        if (!tableExists(connection, "Players")) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE Players (PlayerID VARCHAR(255) NOT NULL, TeamID VARCHAR(255), JerseyNumber INTEGER, Code VARCHAR(255), LastName VARCHAR(255), FirstName VARCHAR(255), FullName VARCHAR(255), Position VARCHAR(255), Starter BIT, Side VARCHAR(20), CONSTRAINT PK_Players PRIMARY KEY (PlayerID))");
            }
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws Exception {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    private void upsertTeam(Connection connection, TeamRecord team) throws Exception {
        String updateSql = "UPDATE Teams SET TeamCode=?, TeamName=?, ShortName=?, Side=? WHERE TeamID=?";

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setString(1, team.teamCode);
            statement.setString(2, team.teamName);
            statement.setString(3, team.shortName);
            statement.setString(4, team.side);
            statement.setString(5, team.teamId);

            if (statement.executeUpdate() > 0) {
                return;
            }
        }

        String insertSql = "INSERT INTO Teams (TeamID, TeamCode, TeamName, ShortName, Side) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setString(1, team.teamId);
            statement.setString(2, team.teamCode);
            statement.setString(3, team.teamName);
            statement.setString(4, team.shortName);
            statement.setString(5, team.side);
            statement.executeUpdate();
        }
    }

	private void upsertPlayer(Connection connection, PlayerRecord player) throws Exception {
	    try {
	        String updateSql = "UPDATE Players SET TeamID=?, JerseyNumber=?, Code=?, LastName=?, FirstName=?, FullName=?, Position=?, Starter=?, Side=? WHERE PlayerID=?";
	
	        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
	            statement.setString(1, player.teamId);
	            statement.setInt(2, player.jerseyNumber);
	            statement.setString(3, player.code);
	            statement.setString(4, player.lastName);
	            statement.setString(5, player.firstName);
	            statement.setString(6, player.fullName);
	            statement.setString(7, player.position);
	            statement.setBoolean(8, player.starter);
	            statement.setString(9, player.side);
	            statement.setString(10, player.playerId);
	
	            if (statement.executeUpdate() > 0) {
	                return;
	            }
	        }
	
	        String insertSql = "INSERT INTO Players (PlayerID, TeamID, JerseyNumber, Code, LastName, FirstName, FullName, Position, Starter, Side) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
	            statement.setString(1, player.playerId);
	            statement.setString(2, player.teamId);
	            statement.setInt(3, player.jerseyNumber);
	            statement.setString(4, player.code);
	            statement.setString(5, player.lastName);
	            statement.setString(6, player.firstName);
	            statement.setString(7, player.fullName);
	            statement.setString(8, player.position);
	            statement.setBoolean(9, player.starter);
	            statement.setString(10, player.side);
	            statement.executeUpdate();
	        }
	    } catch (Exception e) {
	        throw new Exception("Player export failed. PlayerID=" + player.playerId + ", TeamID=" + player.teamId + ", Jersey=" + player.jerseyNumber + ", Name=" + player.fullName + ", Code=" + player.code + ", Position=" + player.position + ", Starter=" + player.starter + ", Side=" + player.side + ". " + e.getMessage(), e);
	    }
	}
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class TeamRecord {
        String teamId;
        String teamCode;
        String teamName;
        String shortName;
        String side;
    }

    private static class PlayerRecord {
        String playerId;
        String teamId;
        int jerseyNumber;
        String code;
        String lastName;
        String firstName;
        String fullName;
        String position;
        boolean starter;
        String side;
    }

    public static class ExportResult {
        private final int matchFiles;
        private final int teams;
        private final int players;

        public ExportResult(int matchFiles, int teams, int players) {
            this.matchFiles = matchFiles;
            this.teams = teams;
            this.players = players;
        }

        public int getMatchFiles() {
            return matchFiles;
        }

        public int getTeams() {
            return teams;
        }

        public int getPlayers() {
            return players;
        }
    }
}