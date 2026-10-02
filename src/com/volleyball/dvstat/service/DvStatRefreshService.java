package com.volleyball.dvstat.service;

import com.volleyball.dvstat.model.MatchInfo;
import com.volleyball.dvstat.model.MatchSetup;
import com.volleyball.dvstat.model.MatchStatistics;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DvStatRefreshService {

    private static final long STABLE_DELAY_MILLISECONDS = 2000L;
    private static final Path CONFIG_FILE = Path.of(System.getProperty("user.home"), ".volleyballstats", "config.properties");
    private static final Path OUTPUT_DIRECTORY = Path.of("C:\\Sports\\Volleyball\\Matches");
    private static final Path SETUP_DIRECTORY = Path.of("C:\\Sports\\Volleyball\\Setup");

    private final DvStatBatchService dvStatBatchService;
    private final MatchJsonService matchJsonService;
    private final MatchSetupJsonService matchSetupJsonService;
    private final DvMatchParser dvMatchParser;
    private final Map<String, Long> processedFiles = new ConcurrentHashMap<>();

    public DvStatRefreshService(DvStatBatchService dvStatBatchService, MatchJsonService matchJsonService, MatchSetupJsonService matchSetupJsonService, DvMatchParser dvMatchParser) {
        this.dvStatBatchService = dvStatBatchService;
        this.matchJsonService = matchJsonService;
        this.matchSetupJsonService = matchSetupJsonService;
        this.dvMatchParser = dvMatchParser;
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduledRefresh() {
        try {
            String dllPath = getConfigValue("dllPath");
            String statisticsFolder = getConfigValue("statisticsFolder");

            if (dllPath == null || dllPath.trim().isEmpty()) return;
            if (statisticsFolder == null || statisticsFolder.trim().isEmpty()) return;
            if (!Files.exists(Path.of(dllPath))) return;
            if (!Files.isDirectory(Path.of(statisticsFolder))) return;

            refresh(dllPath, statisticsFolder, OUTPUT_DIRECTORY.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh(String dllPath, String statisticsFolder, String outputFolder) {
        File folder = new File(statisticsFolder);

        if (!folder.isDirectory() || !hasStatisticsFilesChanged(folder)) return;

        File[] dvwFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".dvw"));

        if (dvwFiles == null || dvwFiles.length == 0) return;

        boolean allProcessed = true;

        for (File dvwFile : dvwFiles) {
            if (!processMatch(dllPath, statisticsFolder, outputFolder, dvwFile)) {
                allProcessed = false;
            }
        }

        if (allProcessed) {
            recordCurrentFileTimestamps(folder);
        }
    }

    private boolean hasStatisticsFilesChanged(File folder) {
        File[] files = getRelevantFiles(folder);

        if (files == null) return false;

        for (File file : files) {
            try {
                Path path = file.toPath();

                if (!Files.isRegularFile(path)) continue;

                long lastModified = Files.getLastModifiedTime(path).toMillis();
                String fileKey = path.toAbsolutePath().toString();
                Long previousModified = processedFiles.get(fileKey);

                if (previousModified == null || previousModified != lastModified) {
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }

        return false;
    }

    private boolean processMatch(String dllPath, String statisticsFolder, String outputFolder, File dvwFile) {
        try {
            Path path = dvwFile.toPath();

            if (!Files.isRegularFile(path)) return true;

            long initialModified = Files.getLastModifiedTime(path).toMillis();

            Thread.sleep(STABLE_DELAY_MILLISECONDS);

            long stableModified = Files.getLastModifiedTime(path).toMillis();

            if (stableModified != initialModified) return false;

            MatchInfo parsedMatchInfo = dvMatchParser.parse(path);

            Path setupFile = matchSetupJsonService.writeSetupJson(parsedMatchInfo, path, SETUP_DIRECTORY);

            MatchSetup matchSetup = matchSetupJsonService.readSetupJson(setupFile);

            MatchStatistics matchStatistics = dvStatBatchService.readMatch(dllPath, statisticsFolder, dvwFile, matchSetup.getMatchInfo());

            matchJsonService.writeMatchJson(matchStatistics, matchSetup.getMatchInfo(), setupFile, Path.of(outputFolder));

            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void recordCurrentFileTimestamps(File folder) {
        File[] files = getRelevantFiles(folder);

        if (files == null) return;

        for (File file : files) {
            try {
                Path path = file.toPath();

                if (!Files.isRegularFile(path)) continue;

                processedFiles.put(path.toAbsolutePath().toString(), Files.getLastModifiedTime(path).toMillis());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File[] getRelevantFiles(File folder) {
        return folder.listFiles((dir, name) -> {
            String fileName = name.toLowerCase();
            return fileName.endsWith(".dvw") || (fileName.endsWith(".tot") && fileName.startsWith("statistics"));
        });
    }

    public void clearProcessedFiles() {
        processedFiles.clear();
    }

    private String getConfigValue(String key) {
        Properties properties = new Properties();

        try {
            if (!Files.exists(CONFIG_FILE)) return null;

            try (java.io.InputStream input = Files.newInputStream(CONFIG_FILE)) {
                properties.load(input);
            }

            return properties.getProperty(key);

        } catch (Exception e) {
            return null;
        }
    }
}