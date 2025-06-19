package com.freshworks.rag.controller;

import com.freshworks.rag.haystack.HaystackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Controller
public class HaystackController {

    @Autowired
    private HaystackService haystackService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        try {
            String question = request.get("question");
            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Question cannot be empty"));
            }
            
            String answer = haystackService.askQuestion(question);
            return ResponseEntity.ok(Map.of("answer", answer));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error processing question: " + e.getMessage()));
        }
    }

    @PostMapping("/api/index")
    @ResponseBody
    public ResponseEntity<Map<String, String>> indexLogs(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Query cannot be empty"));
            }
            
            haystackService.indexLogs(query);
            return ResponseEntity.ok(Map.of("message", "Logs indexed successfully for query: " + query));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error indexing logs: " + e.getMessage()));
        }
    }

    @PostMapping("/api/clear")
    @ResponseBody
    public ResponseEntity<Map<String, String>> clearLogs() {
        try {
            haystackService.clearLogs();
            return ResponseEntity.ok(Map.of("message", "All logs cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error clearing logs: " + e.getMessage()));
        }
    }
} 