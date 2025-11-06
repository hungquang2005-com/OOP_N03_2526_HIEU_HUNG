package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.Locale;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueStats(
        @RequestParam String period,
        @RequestParam(required = false) String date,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month
    ) {
        try {
            List<Order> allOrders = orderRepository.findAll();
            double totalRevenue = 0;
            int totalOrders = 0;
            List<Map<String, Object>> chartData = new ArrayList<>();

            switch (period) {
                case "daily":
                    if (date == null) return ResponseEntity.badRequest().body("Thiếu 'date' cho period 'daily'");
                    LocalDate targetDate = LocalDate.parse(date);
                    
                    List<Order> dailyOrders = allOrders.stream()
                        .filter(order -> order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(targetDate))
                        .collect(Collectors.toList());
                    
                    totalRevenue = dailyOrders.stream().mapToDouble(Order::getTotal).sum();
                    totalOrders = dailyOrders.size();
                    
                    Map<Integer, Double> hourlyRevenue = new HashMap<>();
                    for (Order order : dailyOrders) {
                        int hour = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).getHour();
                        hourlyRevenue.put(hour, hourlyRevenue.getOrDefault(hour, 0.0) + order.getTotal());
                    }
                    
                    for (int h = 0; h < 24; h++) {
                        Map<String, Object> dataPoint = new HashMap<>();
                        dataPoint.put("label", h + ":00");
                        dataPoint.put("revenue", hourlyRevenue.getOrDefault(h, 0.0));
                        chartData.add(dataPoint);
                    }
                    break;

                case "weekly":
                    if (date == null) return ResponseEntity.badRequest().body("Thiếu 'date' cho period 'weekly'");
                    LocalDate weeklyDate = LocalDate.parse(date);
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int targetWeek = weeklyDate.get(weekFields.weekOfWeekBasedYear());
                    int targetYear = weeklyDate.get(weekFields.weekBasedYear());

                    List<Order> weeklyOrders = allOrders.stream()
                        .filter(order -> {
                            LocalDate orderDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return orderDate.get(weekFields.weekBasedYear()) == targetYear &&
                                   orderDate.get(weekFields.weekOfWeekBasedYear()) == targetWeek;
                        })
                        .collect(Collectors.toList());

                    totalRevenue = weeklyOrders.stream().mapToDouble(Order::getTotal).sum();
                    totalOrders = weeklyOrders.size();
                    
                    Map<String, Double> dailyRevenueInWeek = new HashMap<>();
                    for (Order order : weeklyOrders) {
                        LocalDate orderDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        String dayLabel = orderDate.getDayOfWeek().toString().substring(0, 3);
                        dailyRevenueInWeek.put(dayLabel, dailyRevenueInWeek.getOrDefault(dayLabel, 0.0) + order.getTotal());
                    }
                    
                    String[] daysOfWeek = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
                    for (String day : daysOfWeek) {
                        Map<String, Object> dataPoint = new HashMap<>();
                        dataPoint.put("label", day);
                        dataPoint.put("revenue", dailyRevenueInWeek.getOrDefault(day, 0.0));
                        chartData.add(dataPoint);
                    }
                    break;

                case "monthly":
                    if (year == null || month == null) return ResponseEntity.badRequest().body("Thiếu 'year' hoặc 'month' cho period 'monthly'");

                    List<Order> monthlyOrders = allOrders.stream()
                        .filter(order -> {
                            LocalDate orderDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return orderDate.getYear() == year && orderDate.getMonthValue() == month;
                        })
                        .collect(Collectors.toList());

                    totalRevenue = monthlyOrders.stream().mapToDouble(Order::getTotal).sum();
                    totalOrders = monthlyOrders.size();
                    
                    Map<Integer, Double> dailyRevenueInMonth = new HashMap<>();
                    for (Order order : monthlyOrders) {
                        LocalDate orderDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int day = orderDate.getDayOfMonth();
                        dailyRevenueInMonth.put(day, dailyRevenueInMonth.getOrDefault(day, 0.0) + order.getTotal());
                    }
                    
                    int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
                    for (int d = 1; d <= daysInMonth; d++) {
                        Map<String, Object> dataPoint = new HashMap<>();
                        dataPoint.put("label", "Ngày " + d);
                        dataPoint.put("revenue", dailyRevenueInMonth.getOrDefault(d, 0.0));
                        chartData.add(dataPoint);
                    }
                    break;
                    
                case "yearly":
                    int currentYear = LocalDate.now().getYear();
                    
                    List<Order> yearlyOrders = allOrders.stream()
                        .filter(order -> {
                            LocalDate orderDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return orderDate.getYear() == currentYear;
                        })
                        .collect(Collectors.toList());

                    totalRevenue = yearlyOrders.stream().mapToDouble(Order::getTotal).sum();
                    totalOrders = yearlyOrders.size();
                    
                    Map<Integer, Double> monthlyRevenueInYear = new HashMap<>();
                    for (Order order : yearlyOrders) {
                        LocalDate orderDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int m = orderDate.getMonthValue();
                        monthlyRevenueInYear.put(m, monthlyRevenueInYear.getOrDefault(m, 0.0) + order.getTotal());
                    }
                    
                    String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                                           "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
                    for (int m = 1; m <= 12; m++) {
                        Map<String, Object> dataPoint = new HashMap<>();
                        dataPoint.put("label", monthNames[m-1]);
                        dataPoint.put("revenue", monthlyRevenueInYear.getOrDefault(m, 0.0));
                        chartData.add(dataPoint);
                    }
                    break;

                case "all":
                    totalRevenue = allOrders.stream().mapToDouble(Order::getTotal).sum();
                    totalOrders = allOrders.size();
                    
                    Map<Integer, Double> yearlyRevenue = new HashMap<>();
                    for (Order order : allOrders) {
                        LocalDate orderDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int y = orderDate.getYear();
                        yearlyRevenue.put(y, yearlyRevenue.getOrDefault(y, 0.0) + order.getTotal());
                    }
                    
                    List<Integer> years = new ArrayList<>(yearlyRevenue.keySet());
                    Collections.sort(years);
                    for (Integer y : years) {
                        Map<String, Object> dataPoint = new HashMap<>();
                        dataPoint.put("label", "Năm " + y);
                        dataPoint.put("revenue", yearlyRevenue.get(y));
                        chartData.add(dataPoint);
                    }
                    break;

                default:
                    return ResponseEntity.badRequest().body("Period không hợp lệ: " + period);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("period", period);
            result.put("totalRevenue", totalRevenue);
            result.put("orderCount", totalOrders);
            result.put("chartData", chartData); 

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("❌ Error in getRevenueStats: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy doanh thu: " + e.getMessage());
        }
    }

    // ===== CÁC HÀM CŨ (GIỮ NGUYÊN) =====
    @GetMapping("/popular-items")
    public ResponseEntity<?> getPopularItems(
            @RequestParam String period,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        try {
            List<Order> allOrders = orderRepository.findAll();
            LocalDate startDate;

            switch (period) {
                case "daily":
                    startDate = LocalDate.parse(date);
                    break;
                case "weekly":
                    LocalDate weeklyDate = LocalDate.parse(date);
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int targetWeek = weeklyDate.get(weekFields.weekOfWeekBasedYear());
                    int targetYear = weeklyDate.get(weekFields.weekBasedYear());
                    startDate = weeklyDate.with(weekFields.weekBasedYear(), targetYear).with(weekFields.weekOfWeekBasedYear(), targetWeek);
                    break;
                case "monthly":
                    startDate = LocalDate.of(year, month, 1);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Period không hợp lệ");
            }

            List<Order> orders = allOrders.stream()
                .filter(order -> {
                    LocalDate orderDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (period.equals("daily")) {
                        return orderDate.equals(startDate);
                    } else if (period.equals("weekly")) {
                        WeekFields weekFields = WeekFields.of(Locale.getDefault());
                        return orderDate.get(weekFields.weekBasedYear()) == startDate.get(weekFields.weekBasedYear()) &&
                               orderDate.get(weekFields.weekOfWeekBasedYear()) == startDate.get(weekFields.weekOfWeekBasedYear());
                    } else if (period.equals("monthly")) {
                        return orderDate.getYear() == year && orderDate.getMonthValue() == month;
                    }
                    return false;
                })
                .collect(Collectors.toList());

            Map<String, Integer> foodQuantityMap = new HashMap<>();
            for (Order order : orders) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    String foodName = detail.getFood().getName();
                    foodQuantityMap.put(foodName,
                            foodQuantityMap.getOrDefault(foodName, 0) + detail.getQuantity());
                }
            }

            List<Map<String, Object>> popularItems = foodQuantityMap.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .map(entry -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("foodName", entry.getKey());
                        item.put("quantity", entry.getValue());
                        return item;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("period", period);
            result.put("startDate", startDate.toString());
            result.put("items", popularItems);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("❌ Error getting popular items: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi lấy thống kê: " + e.getMessage());
        }
    }

    @GetMapping("/least-popular-items")
    public ResponseEntity<?> getLeastPopularItems(
            @RequestParam String period,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        try {
            List<Order> allOrders = orderRepository.findAll();
            LocalDate startDate;

            switch (period) {
                case "daily":
                    startDate = LocalDate.parse(date);
                    break;
                case "weekly":
                    LocalDate weeklyDate = LocalDate.parse(date);
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int targetWeek = weeklyDate.get(weekFields.weekOfWeekBasedYear());
                    int targetYear = weeklyDate.get(weekFields.weekBasedYear());
                    startDate = weeklyDate.with(weekFields.weekBasedYear(), targetYear).with(weekFields.weekOfWeekBasedYear(), targetWeek);
                    break;
                case "monthly":
                    startDate = LocalDate.of(year, month, 1);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Period không hợp lệ");
            }

            List<Order> orders = allOrders.stream()
                .filter(order -> {
                    LocalDate orderDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (period.equals("daily")) {
                        return orderDate.equals(startDate);
                    } else if (period.equals("weekly")) {
                        WeekFields weekFields = WeekFields.of(Locale.getDefault());
                        return orderDate.get(weekFields.weekBasedYear()) == startDate.get(weekFields.weekBasedYear()) &&
                               orderDate.get(weekFields.weekOfWeekBasedYear()) == startDate.get(weekFields.weekOfWeekBasedYear());
                    } else if (period.equals("monthly")) {
                        return orderDate.getYear() == year && orderDate.getMonthValue() == month;
                    }
                    return false;
                })
                .collect(Collectors.toList());

            Map<String, Integer> foodQuantityMap = new HashMap<>();
            for (Order order : orders) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    String foodName = detail.getFood().getName();
                    foodQuantityMap.put(foodName,
                        foodQuantityMap.getOrDefault(foodName, 0) + detail.getQuantity());
                }
            }

            List<Map<String, Object>> leastPopularItems = foodQuantityMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(10)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("foodName", entry.getKey());
                    item.put("quantity", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("period", period);
            result.put("startDate", startDate.toString());
            result.put("items", leastPopularItems);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("❌ Error getting least popular items: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi lấy thống kê: " + e.getMessage());
        }
    }
}