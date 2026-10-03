package com.volleyball.controller;

import com.volleyball.dvstat.model.MatchSetup;
import com.volleyball.dvstat.model.MatchStatistics;
import com.volleyball.dvstat.service.DvMatchParser;
import com.volleyball.dvstat.service.MasterDataExportService;
import com.volleyball.dvstat.service.MatchJsonService;
import com.volleyball.dvstat.service.MatchSetupJsonService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

@Controller
public class VolleyballController {

	@Autowired
	private MasterDataExportService masterDataExportService;
	
    @Autowired
    private MatchSetupJsonService matchSetupJsonService;

    @Autowired
    private DvMatchParser dvMatchParser;

    @Autowired
    private MatchJsonService matchJsonService;
    
    private static final Path DLL_DIRECTORY = Path.of(System.getProperty("user.home"), ".volleyballstats");
    private static final Path DLL_FILE = DLL_DIRECTORY.resolve("DvStat.dll");
    private static final Path CONFIG_FILE = DLL_DIRECTORY.resolve("config.properties");
    private static final Path SETUP_DIRECTORY = Path.of("C:\\Sports\\Volleyball\\Setup");
    private static final Path MATCH_DIRECTORY = Path.of("C:\\Sports\\Volleyball\\Matches");
    
    @PostMapping("/export-master-data")
    public String exportMasterData(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            String statisticsFolder = (String) session.getAttribute("statisticsFolder");

            if (statisticsFolder == null || statisticsFolder.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("exportError", "Statistics folder is not configured.");
                return "redirect:/index";
            }

            MasterDataExportService.ExportResult result = masterDataExportService.export(statisticsFolder);

            redirectAttributes.addFlashAttribute("exportMessage", "Export completed. Matches: " + result.getMatchFiles() + ", Teams: " + result.getTeams() + ", Players: " + result.getPlayers());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("exportError", "Export failed: " + e.getMessage());
        }

        return "redirect:/index";
    }    
    
	@GetMapping("/match-status")
	@ResponseBody
	public long matchStatus(HttpSession session) {
	    try {
	        String statisticsFolder = (String) session.getAttribute("statisticsFolder");
	
	        if (statisticsFolder == null || statisticsFolder.trim().isEmpty()) {
	            Properties properties = loadConfig();
	            statisticsFolder = properties.getProperty("statisticsFolder", "");
	        }
	
	        if (statisticsFolder == null || statisticsFolder.trim().isEmpty()) {
	            return 0L;
	        }
	
	        File statisticsDirectory = new File(statisticsFolder);
	
	        File[] dvwFiles = statisticsDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".dvw"));
	
	        if (dvwFiles == null || dvwFiles.length == 0) {
	            return 0L;
	        }
	
	        File latestMatch = Arrays.stream(dvwFiles)
	                .max(Comparator.comparingLong(File::lastModified))
	                .orElse(null);
	
	        if (latestMatch == null) {
	            return 0L;
	        }
	
	        var matchInfo = dvMatchParser.parse(latestMatch.toPath());
	
	        String matchFileName = matchJsonService.buildFileName(matchInfo) + ".json";
	        Path matchJsonFile = MATCH_DIRECTORY.resolve(matchFileName);
	
	        if (!Files.exists(matchJsonFile)) {
	            return 0L;
	        }
	
	        return Files.getLastModifiedTime(matchJsonFile).toMillis();
	
	    } catch (Exception e) {
	        return 0L;
	    }
	}    
    @GetMapping("/")
    public String initialise(Model model) {
        Properties properties = loadConfig();
        model.addAttribute("statisticsFolder", properties.getProperty("statisticsFolder", ""));
        model.addAttribute("dllPath", properties.getProperty("dllPath", DLL_FILE.toAbsolutePath().toString()));
        return "initialise";
    }

    @PostMapping("/initialize")
    public String initialize(@RequestParam(value = "dllFile", required = false) MultipartFile dllFile, @RequestParam("statisticsFolder") String statisticsFolder, HttpSession session, Model model) {
        try {
            if (statisticsFolder == null || statisticsFolder.trim().isEmpty()) {
                throw new IllegalArgumentException("Please enter the Data Volley / Click&Scout statistics folder.");
            }

            File statsFolder = new File(statisticsFolder.trim());

            if (!statsFolder.isDirectory()) {
                throw new IllegalArgumentException("Statistics folder was not found: " + statisticsFolder);
            }

            String dllPath;

            if (dllFile != null && !dllFile.isEmpty()) {
                String originalFileName = dllFile.getOriginalFilename();

                if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".dll")) {
                    throw new IllegalArgumentException("Please select a .dll file.");
                }

                Files.createDirectories(DLL_DIRECTORY);
                Files.copy(dllFile.getInputStream(), DLL_FILE, StandardCopyOption.REPLACE_EXISTING);
                dllPath = DLL_FILE.toAbsolutePath().toString();
            } else {
                Properties properties = loadConfig();
                dllPath = properties.getProperty("dllPath", DLL_FILE.toAbsolutePath().toString());
            }

            Path selectedDll = Path.of(dllPath);

            if (!Files.exists(selectedDll)) {
                throw new IllegalArgumentException("DvStat.dll could not be found. Please select the DLL again.");
            }

            session.setAttribute("dllPath", selectedDll.toAbsolutePath().toString());
            session.setAttribute("statisticsFolder", statsFolder.getAbsolutePath());

            saveConfig(selectedDll.toAbsolutePath().toString(), statsFolder.getAbsolutePath());

            return "redirect:/index";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("statisticsFolder", statisticsFolder);

            Properties properties = loadConfig();

            model.addAttribute("dllPath", properties.getProperty("dllPath", DLL_FILE.toAbsolutePath().toString()));

            return "initialise";
        }
    }

    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        String dllPath = (String) session.getAttribute("dllPath");
        String statisticsFolder = (String) session.getAttribute("statisticsFolder");

        if (dllPath == null || statisticsFolder == null) {
            Properties properties = loadConfig();
            dllPath = properties.getProperty("dllPath", DLL_FILE.toAbsolutePath().toString());
            statisticsFolder = properties.getProperty("statisticsFolder", "");
        }

        if (dllPath == null || dllPath.trim().isEmpty() || statisticsFolder == null || statisticsFolder.trim().isEmpty()) {
            return "redirect:/";
        }

        if (!Files.exists(Path.of(dllPath))) {
            return "redirect:/";
        }

        try {
            File statisticsDirectory = new File(statisticsFolder);

            File[] dvwFiles = statisticsDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".dvw"));

            if (dvwFiles == null || dvwFiles.length == 0) {
                model.addAttribute("error", "No .dvw match files were found in: " + statisticsFolder);
                return "index";
            }

            File latestMatch = Arrays.stream(dvwFiles)
                    .max(Comparator.comparingLong(File::lastModified))
                    .orElse(null);

            if (latestMatch == null) {
                model.addAttribute("error", "No match file could be selected.");
                return "index";
            }

            var parsedMatchInfo = dvMatchParser.parse(latestMatch.toPath());
            String setupFileName = matchSetupJsonService.buildFileName(parsedMatchInfo) + ".json";
            Path setupFile = SETUP_DIRECTORY.resolve(setupFileName);

            if (!Files.exists(setupFile)) {
                matchSetupJsonService.writeSetupJson(parsedMatchInfo, latestMatch.toPath(), SETUP_DIRECTORY);
            }

            MatchSetup matchSetup = matchSetupJsonService.readSetupJson(setupFile);

            String matchFileName = matchJsonService.buildFileName(matchSetup.getMatchInfo()) + ".json";
            Path matchJsonFile = MATCH_DIRECTORY.resolve(matchFileName);

            if (!Files.exists(matchJsonFile)) {
                model.addAttribute("error", "Match JSON file was not found: " + matchJsonFile);
                model.addAttribute("dllPath", dllPath);
                model.addAttribute("statisticsFolder", statisticsFolder);
                return "index";
            }

            MatchStatistics matchStatistics = matchJsonService.readMatchJson(matchJsonFile);

            model.addAttribute("matchSetup", matchSetup);
            model.addAttribute("matchStatistics", matchStatistics);
            
            model.addAttribute("dllPath", dllPath);
            model.addAttribute("statisticsFolder", statisticsFolder);
            model.addAttribute("matchFile", latestMatch.getAbsolutePath());
            model.addAttribute("setupFile", setupFile.toAbsolutePath().toString());

            return "index";

        } catch (Exception e) {
            model.addAttribute("error", e.getClass().getSimpleName() + ": " + e.getMessage());
            model.addAttribute("dllPath", dllPath);
            model.addAttribute("statisticsFolder", statisticsFolder);
            return "index";
        }
    }

    private Properties loadConfig() {
        Properties properties = new Properties();

        try {
            if (Files.exists(CONFIG_FILE)) {
                try (InputStream input = Files.newInputStream(CONFIG_FILE)) {
                    properties.load(input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private void saveConfig(String dllPath, String statisticsFolder) {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());

            Properties properties = new Properties();
            properties.setProperty("dllPath", dllPath);
            properties.setProperty("statisticsFolder", statisticsFolder);

            try (OutputStream output = Files.newOutputStream(CONFIG_FILE)) {
                properties.store(output, "VolleyballStats Configuration");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}