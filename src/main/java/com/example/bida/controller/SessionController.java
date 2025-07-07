package com.example.bida.controller;

import com.example.bida.entity.Session;
import com.example.bida.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "http://localhost:5173") // ⚠️ Đảm bảo đúng port của React frontend
public class SessionController {

    @Autowired
    private SessionService sessionService;

    // Bắt đầu phiên chơi
    @PostMapping("/start/{tableId}")
    public Session startSession(@PathVariable Long tableId) {
        return sessionService.startSession(tableId);
    }

    // ✅ Kết thúc phiên chơi dựa trên tableId
    @PostMapping("/end/{tableId}")
    public Session endSessionByTable(@PathVariable Long tableId) {
        return sessionService.endSessionByTableId(tableId);
    }
}
