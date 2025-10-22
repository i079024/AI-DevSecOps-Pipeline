package com.example.aidevops.controller;

import com.example.aidevops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic statistics
        stats.put("totalUsers", userService.getUserCount());
        stats.put("systemStatus", "Operational");
        stats.put("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Memory information
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("totalMemory", formatBytes(runtime.totalMemory()));
        memoryInfo.put("freeMemory", formatBytes(runtime.freeMemory()));
        memoryInfo.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        memoryInfo.put("maxMemory", formatBytes(runtime.maxMemory()));
        stats.put("memory", memoryInfo);
        
        // System properties
        Map<String, String> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("osArchitecture", System.getProperty("os.arch"));
        stats.put("system", systemInfo);
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/users/reset")
    public ResponseEntity<Map<String, Object>> resetUserData() {
        // This would reset all user data - use with caution!
        // For demo purposes, we'll just return a message
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User data reset operation");
        response.put("warning", "This operation would delete all users in a real implementation");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/logs")
    public ResponseEntity<Map<String, Object>> getSystemLogs() {
        Map<String, Object> logs = new HashMap<>();
        
        // Simulated log entries
        logs.put("lastAccess", LocalDateTime.now().minusMinutes(5));
        logs.put("activeConnections", 1);
        logs.put("requestCount", 42);
        logs.put("errorCount", 0);
        logs.put("status", "All systems operational");
        
        return ResponseEntity.ok(logs);
    }
    
    @PostMapping("/maintenance")
    public ResponseEntity<Map<String, String>> triggerMaintenance(@RequestParam String action) {
        Map<String, String> response = new HashMap<>();
        
        switch (action.toLowerCase()) {
            case "gc":
                System.gc(); // Suggest garbage collection
                response.put("message", "Garbage collection suggested");
                break;
            case "clear-cache":
                response.put("message", "Cache cleared (simulated)");
                break;
            case "refresh-config":
                response.put("message", "Configuration refreshed (simulated)");
                break;
            default:
                response.put("error", "Unknown maintenance action: " + action);
                return ResponseEntity.badRequest().body(response);
        }
        
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getApplicationConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // Application configuration
        config.put("appName", "AI DevSecOps Pipeline");
        config.put("version", "1.0.0");
        config.put("environment", "development");
        config.put("database", "H2 In-Memory");
        config.put("port", 8081);
        
        // Feature flags
        Map<String, Boolean> features = new HashMap<>();
        features.put("userManagement", true);
        features.put("bulkOperations", true);
        features.put("searchEnabled", true);
        features.put("paginationEnabled", true);
        config.put("features", features);
        
        return ResponseEntity.ok(config);
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        return String.format("%.1f %s", bytes / Math.pow(1024, exp), units[exp]);
    }
}