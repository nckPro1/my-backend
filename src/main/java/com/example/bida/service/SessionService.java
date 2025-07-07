package com.example.bida.service;

import com.example.bida.entity.Session;
import com.example.bida.entity.Table;
import com.example.bida.entity.TableStatus;
import com.example.bida.repository.SessionRepository;
import com.example.bida.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TableRepository tableRepository;

    // ✅ Bắt đầu phiên chơi
    public Session startSession(Long tableId) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));

        if (table.getStatus() == TableStatus.PLAYING) {
            throw new RuntimeException("Bàn đang được sử dụng");
        }

        Session session = new Session();
        session.setTable(table);
        session.setStartTime(LocalDateTime.now());

        table.setStatus(TableStatus.PLAYING);
        tableRepository.save(table);

        return sessionRepository.save(session);
    }

    // ✅ Kết thúc phiên chơi theo sessionId (nội bộ dùng)
    public Session endSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên"));

        if (session.getEndTime() != null) {
            throw new RuntimeException("Phiên đã kết thúc");
        }

        LocalDateTime endTime = LocalDateTime.now();
        session.setEndTime(endTime);

        // 🎯 Tính toán thời gian và giá tiền dựa trên pricePerHour của table
        Duration duration = Duration.between(session.getStartTime(), endTime);
        long totalMinutes = duration.toMinutes();

        // 💰 Sử dụng giá từ table thay vì hardcode
        Table table = session.getTable();
        double pricePerMinute = table.getPricePerHour() / 60.0;
        double totalPrice = totalMinutes * pricePerMinute;

        session.setTotalMinutes(totalMinutes);
        session.setTotalPrice(totalPrice);

        table.setStatus(TableStatus.AVAILABLE);
        tableRepository.save(table);

        return sessionRepository.save(session);
    }

    // ✅ Kết thúc phiên chơi theo tableId (dùng từ controller)
    public Session endSessionByTableId(Long tableId) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));

        Session session = sessionRepository.findByTableAndEndTimeIsNull(table)
                .orElseThrow(() -> new RuntimeException("Bàn này không có phiên đang chơi"));

        return endSession(session.getId());
    }

    // 🆕 Lấy session đang chơi của bàn (để frontend hiển thị thông tin)
    public Session getActiveSession(Long tableId) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));

        return sessionRepository.findByTableAndEndTimeIsNull(table)
                .orElse(null);
    }

    // 🆕 Tính toán realtime cho frontend
    public double calculateCurrentPrice(Long tableId) {
        Session session = getActiveSession(tableId);
        if (session == null) return 0.0;

        Duration duration = Duration.between(session.getStartTime(), LocalDateTime.now());
        long currentMinutes = duration.toMinutes();

        Table table = session.getTable();
        double pricePerMinute = table.getPricePerHour() / 60.0;

        return currentMinutes * pricePerMinute;
    }

    // 🆕 Tính thời gian chơi hiện tại (phút)
    public long calculateCurrentMinutes(Long tableId) {
        Session session = getActiveSession(tableId);
        if (session == null) return 0L;

        Duration duration = Duration.between(session.getStartTime(), LocalDateTime.now());
        return duration.toMinutes();
    }
}