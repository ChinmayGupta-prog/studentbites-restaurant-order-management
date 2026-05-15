package com.studentbites.service;

import com.studentbites.dto.CartItem;
import com.studentbites.dto.CheckoutForm;
import com.studentbites.model.OrderLine;
import com.studentbites.model.OrderStatus;
import com.studentbites.model.StudentOrder;
import com.studentbites.repository.StudentOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final StudentOrderRepository orders;

    public OrderService(StudentOrderRepository orders) {
        this.orders = orders;
    }

    public StudentOrder placeOrder(CheckoutForm form, List<CartItem> cartItems) {
        StudentOrder order = new StudentOrder();
        order.setStudentName(form.getStudentName());
        order.setEmail(form.getEmail());
        order.setPhone(form.getPhone());
        order.setHostelOrClass(form.getHostelOrClass());
        order.setOrderMode(form.getOrderMode());
        order.setPaymentMode(form.getPaymentMode());
        order.setPaymentReference("SIM-" + System.currentTimeMillis());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            OrderLine line = new OrderLine();
            line.setFoodItem(cartItem.getFoodItem());
            line.setQuantity(cartItem.getQuantity());
            line.setLineTotal(cartItem.getLineTotal());
            order.addLine(line);
            total = total.add(cartItem.getLineTotal());
        }
        order.setTotal(total);
        return orders.save(order);
    }

    public Optional<StudentOrder> findByIdWithLiveStatus(Long id) {
        return orders.findById(id).map(this::refreshStatus);
    }

    public List<StudentOrder> latestForStudent(String email) {
        return refreshStatuses(orders.findTop8ByEmailIgnoreCaseOrderByCreatedAtDesc(email));
    }

    public StudentOrder refreshStatus(StudentOrder order) {
        OrderStatus liveStatus = statusFor(order.getCreatedAt());
        if (liveStatus.ordinal() > order.getStatus().ordinal()) {
            order.setStatus(liveStatus);
            return orders.save(order);
        }
        return order;
    }

    public List<StudentOrder> refreshStatuses(List<StudentOrder> studentOrders) {
        return studentOrders.stream()
                .map(this::refreshStatus)
                .toList();
    }

    OrderStatus statusFor(LocalDateTime createdAt) {
        long minutes = Duration.between(createdAt, LocalDateTime.now()).toMinutes();
        if (minutes >= 6) {
            return OrderStatus.DELIVERED;
        }
        if (minutes >= 3) {
            return OrderStatus.READY;
        }
        if (minutes >= 1) {
            return OrderStatus.PREPARING;
        }
        return OrderStatus.PENDING;
    }
}
