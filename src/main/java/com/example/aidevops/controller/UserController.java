package com.example.aidevops.controller;

import com.example.aidevops.model.User;
import com.example.aidevops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir) {
        List<User> users = userService.getAllUsers(sortBy, sortDir);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<User> userPage = userService.getUsersPaginated(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        response.put("hasNext", userPage.hasNext());
        response.put("hasPrevious", userPage.hasPrevious());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                               .body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            response.put("deletedUserId", String.valueOf(id));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("userId", String.valueOf(id));
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/import/json")
    public ResponseEntity<?> importUsersFromJson(@RequestParam("file") MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<User> users = mapper.readValue(file.getInputStream(),
                mapper.getTypeFactory().constructCollectionType(List.class, User.class));
            
            List<User> importedUsers = new ArrayList<>();
            for (User user : users) {
                try {
                    importedUsers.add(userService.createUser(user));
                } catch (RuntimeException e) {
                    // Log the error and continue with next user
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Users imported successfully");
            response.put("importedCount", importedUsers.size());
            response.put("totalCount", users.size());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                               .body("Error importing users: " + e.getMessage());
        }
    }

    @PostMapping("/import/csv")
    public ResponseEntity<?> importUsersFromCsv(@RequestParam("file") MultipartFile file) {
        try {
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = CsvSchema.builder()
                .addColumn("name")
                .addColumn("email")
                .addColumn("password")
                .build()
                .withHeader();

            MappingIterator<User> userIterator = mapper
                .readerFor(User.class)
                .with(schema)
                .readValues(file.getInputStream());

            List<User> importedUsers = new ArrayList<>();
            while (userIterator.hasNext()) {
                try {
                    User user = userIterator.next();
                    importedUsers.add(userService.createUser(user));
                } catch (RuntimeException e) {
                    // Log the error and continue with next user
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Users imported successfully from CSV");
            response.put("importedCount", importedUsers.size());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                               .body("Error importing users from CSV: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        long count = userService.getUserCount();
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", count);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            User updatedUser = userService.partialUpdateUser(id, updates);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, Object>> deleteMultipleUsers(@RequestBody List<Long> userIds) {
        try {
            int deletedCount = userService.deleteMultipleUsers(userIds);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Users deleted successfully");
            response.put("deletedCount", deletedCount);
            response.put("requestedIds", userIds);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/download/json")
    public ResponseEntity<byte[]> downloadUsersAsJson() {
        try {
            List<User> users = userService.getAllUsers(null, null);
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // For LocalDateTime serialization
            String jsonData = mapper.writeValueAsString(users);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "users_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(jsonData.getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating JSON download: " + e.getMessage()).getBytes());
        }
    }
    
    @GetMapping("/download/csv")
    public ResponseEntity<byte[]> downloadUsersAsCsv() {
        try {
            List<User> users = userService.getAllUsers(null, null);
            
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = CsvSchema.builder()
                .addColumn("id", CsvSchema.ColumnType.NUMBER)
                .addColumn("name", CsvSchema.ColumnType.STRING)
                .addColumn("email", CsvSchema.ColumnType.STRING)
                .addColumn("createdAt", CsvSchema.ColumnType.STRING)
                .build()
                .withHeader();
            
            StringWriter writer = new StringWriter();
            mapper.writerFor(User.class)
                .with(schema)
                .writeValues(writer)
                .writeAll(users);
            
            String csvData = writer.toString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "users_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData.getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating CSV download: " + e.getMessage()).getBytes());
        }
    }
    
    @GetMapping("/download/filtered/json")
    public ResponseEntity<byte[]> downloadFilteredUsersAsJson(@RequestParam(required = false) String query) {
        try {
            List<User> users;
            if (query != null && !query.trim().isEmpty()) {
                users = userService.searchUsers(query);
            } else {
                users = userService.getAllUsers(null, null);
            }
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // For LocalDateTime serialization
            String jsonData = mapper.writeValueAsString(users);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String fileName = "filtered_users_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json";
            headers.setContentDispositionFormData("attachment", fileName);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(jsonData.getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating filtered JSON download: " + e.getMessage()).getBytes());
        }
    }
    
    @GetMapping("/download/filtered/csv")
    public ResponseEntity<byte[]> downloadFilteredUsersAsCsv(@RequestParam(required = false) String query) {
        try {
            List<User> users;
            if (query != null && !query.trim().isEmpty()) {
                users = userService.searchUsers(query);
            } else {
                users = userService.getAllUsers(null, null);
            }
            
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = CsvSchema.builder()
                .addColumn("id", CsvSchema.ColumnType.NUMBER)
                .addColumn("name", CsvSchema.ColumnType.STRING)
                .addColumn("email", CsvSchema.ColumnType.STRING)
                .addColumn("createdAt", CsvSchema.ColumnType.STRING)
                .build()
                .withHeader();
            
            StringWriter writer = new StringWriter();
            mapper.writerFor(User.class)
                .with(schema)
                .writeValues(writer)
                .writeAll(users);
            
            String csvData = writer.toString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            String fileName = "filtered_users_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
            headers.setContentDispositionFormData("attachment", fileName);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData.getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating filtered CSV download: " + e.getMessage()).getBytes());
        }
    }
}