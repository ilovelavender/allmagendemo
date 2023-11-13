package com.olegrubin.allmagendemo.controller;

import com.olegrubin.allmagendemo.data.DemoDataManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Only for demo purposes
 */
@RestController
@RequestMapping("/api/demo-data")
public class DemoDataController {

    private final DemoDataManager demoDataManager;

    public DemoDataController(DemoDataManager demoDataManager) {
        this.demoDataManager = demoDataManager;
    }

    @PostMapping("/load")
    public ResponseEntity<?> loadData() {
        demoDataManager.loadData();
        return ResponseEntity.ok("loaded");
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearData() {
        demoDataManager.clearData();
        return ResponseEntity.ok("cleared");
    }
}
