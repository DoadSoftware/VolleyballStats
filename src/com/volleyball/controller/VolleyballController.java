package com.volleyball.controller;

import com.volleyball.dvstat.service.DvStatService;
import com.volleyball.dvstat.service.VolleyballStats;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

@Controller
public class VolleyballController {

    @Autowired
    private DvStatService dvStatService;

    private static final Path DLL_DIRECTORY = Path.of(System.getProperty("user.home"), ".volleyballstats");
    private static final Path DLL_FILE = DLL_DIRECTORY.resolve("DvStat.dll");
    private static final Path CONFIG_FILE = DLL_DIRECTORY.resolve("config.properties");

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

        int team = 0;
        int player = 100;
        int skill = 0;
        int setNumber = 0;

        VolleyballStats stats = dvStatService.readStatistics(dllPath, statisticsFolder, team, player, skill, setNumber);

        model.addAttribute("stats", stats);
        model.addAttribute("dllPath", dllPath);
        model.addAttribute("statisticsFolder", statisticsFolder);
        model.addAttribute("team", team);
        model.addAttribute("player", player);
        model.addAttribute("skill", skill);
        model.addAttribute("setNumber", setNumber);

        return "index";
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