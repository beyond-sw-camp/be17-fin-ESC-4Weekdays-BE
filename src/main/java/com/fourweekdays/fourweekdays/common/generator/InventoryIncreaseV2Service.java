package com.fourweekdays.fourweekdays.common.generator;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryIncreaseV2Service {

    private final Map<String, Integer> fakeTable = new HashMap<>();

    public synchronized void increase(String key, int qty) {
        int current = fakeTable.getOrDefault(key, 0);
        fakeTable.put(key, current + qty);
    }

    public int get(String key) {
        return fakeTable.getOrDefault(key, 0);
    }
}