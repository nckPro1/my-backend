package com.example.bida.controller;

import com.example.bida.entity.Table;
import com.example.bida.entity.TableStatus;
import com.example.bida.entity.Session;
import com.example.bida.repository.SessionRepository;
import com.example.bida.repository.TableRepository;
import com.example.bida.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionService sessionService;

    // 🆕 DTO để trả về thông tin bàn kèm session
    public static class TableWithSessionInfo {
        private Long id;
        private String name;
        private TableStatus status;
        private double pricePerHour;
        private Session currentSession;

        // Constructor
        public TableWithSessionInfo(Table table, Session session) {
            this.id = table.getId();
            this.name = table.getName();
            this.status = table.getStatus();
            this.pricePerHour = table.getPricePerHour();
            this.currentSession = session;
        }

        // Getters
        public Long getId() { return id; }
        public String getName() { return name; }
        public TableStatus getStatus() { return status; }
        public double getPricePerHour() { return pricePerHour; }
        public Session getCurrentSession() { return currentSession; }
    }

    // ✅ GET: Lấy danh sách bàn kèm thông tin session
    @GetMapping
    public List<TableWithSessionInfo> getAllTables() {
        List<Table> tables = tableRepository.findAll();
        return tables.stream()
                .map(table -> {
                    Session activeSession = sessionService.getActiveSession(table.getId());
                    return new TableWithSessionInfo(table, activeSession);
                })
                .toList();
    }

    // ✅ POST: Thêm bàn mới (cần pricePerHour)
    @PostMapping
    public Table addTable(@RequestBody Table table) {
        table.setStatus(TableStatus.AVAILABLE);
        // Đảm bảo có giá mặc định nếu không được cung cấp
        if (table.getPricePerHour() <= 0) {
            table.setPricePerHour(50000.0); // Giá mặc định 50k/giờ
        }
        return tableRepository.save(table);
    }

    // ✅ PUT: Cập nhật bàn (tên, giá)
    @PutMapping("/{id}")
    public ResponseEntity<Table> updateTable(@PathVariable Long id, @RequestBody Table updatedTable) {
        return tableRepository.findById(id)
                .map(table -> {
                    table.setName(updatedTable.getName());
                    table.setPricePerHour(updatedTable.getPricePerHour());
                    // Không cho phép update status trực tiếp, phải qua session
                    return ResponseEntity.ok(tableRepository.save(table));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 🆕 GET: Lấy thông tin realtime của bàn
    @GetMapping("/{id}/realtime")
    public ResponseEntity<Map<String, Object>> getRealtimeInfo(@PathVariable Long id) {
        try {
            long currentMinutes = sessionService.calculateCurrentMinutes(id);
            double currentPrice = sessionService.calculateCurrentPrice(id);

            Map<String, Object> info = new HashMap<>();
            info.put("currentMinutes", currentMinutes);
            info.put("currentPrice", currentPrice);

            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ DELETE: Xoá bàn – đã xử lý xoá session trước
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        Optional<Table> tableOpt = tableRepository.findById(id);
        if (tableOpt.isPresent()) {
            Table table = tableOpt.get();

            // 🧨 Xoá tất cả các session liên quan tới bàn trước
            sessionRepository.deleteAll(sessionRepository.findByTable(table));

            tableRepository.delete(table);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}