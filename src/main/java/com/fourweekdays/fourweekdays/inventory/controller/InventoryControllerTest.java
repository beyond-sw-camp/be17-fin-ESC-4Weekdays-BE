//package com.fourweekdays.fourweekdays.inventory.controller;
//
//import com.fourweekdays.fourweekdays.inventory.service.InventoryServiceTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/inventory")
//public class InventoryControllerTest {
//
//    @Autowired
//    private InventoryServiceTest inventoryServiceTest;
//
//    @GetMapping("/v1")
//    public ResponseEntity<String> runV1Test() throws InterruptedException {
//        inventoryServiceTest.runV1Test();
//        return ResponseEntity.ok("V1 Test Completed");
//    }
//
//    @GetMapping("/v2")
//    public ResponseEntity<String> runV2Test() throws InterruptedException {
//        inventoryServiceTest.runV2Test();
//        return ResponseEntity.ok("V2 Test Completed");
//    }
//
//    @GetMapping("/v2-1")
//    public ResponseEntity<String> runV2_1Test() throws InterruptedException {
//        inventoryServiceTest.runV2_1Test();
//        return ResponseEntity.ok("V2-1 Test Completed");
//    }
//
//        @GetMapping("/v3")
//    public ResponseEntity<String> runV3Test() throws InterruptedException {
//        inventoryServiceTest.runV3Test();
//        return ResponseEntity.ok("V3 Test Completed");
//    }
//}
