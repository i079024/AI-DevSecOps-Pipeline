package com.example.aidevops.controller;

import com.example.aidevops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String index(Model model) {
        // Add some model attributes for the view
        model.addAttribute("appName", "AI DevSecOps Pipeline");
        model.addAttribute("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.addAttribute("userCount", userService.getUserCount());
        return "index";
    }
    
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("appName", "AI DevSecOps Pipeline");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("description", "Sample Spring Boot application with JavaScript frontend");
        return "about";
    }
    
    @GetMapping("/api/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getApplicationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("application", "AI DevSecOps Pipeline");
        status.put("version", "1.0.0");
        status.put("status", "running");
        status.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        status.put("totalUsers", userService.getUserCount());
        status.put("uptime", getUptime());
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/api/health-check")
    @ResponseBody
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "UserService");
        health.put("database", "H2 In-Memory");
        health.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(health);
    }
    
    private String getUptime() {
        // Simple uptime calculation (in a real app, you'd track actual startup time)
        long uptimeMillis = System.currentTimeMillis() % (24 * 60 * 60 * 1000); // Reset daily
        long hours = uptimeMillis / (60 * 60 * 1000);
        long minutes = (uptimeMillis % (60 * 60 * 1000)) / (60 * 1000);
        return String.format("%d hours, %d minutes", hours, minutes);
    }
}