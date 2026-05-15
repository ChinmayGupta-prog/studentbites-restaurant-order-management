package com.studentbites.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CheckoutForm {
    @NotBlank
    private String studentName;

    @Email
    private String email;

    @NotBlank
    private String phone;

    private String hostelOrClass;
    private String orderMode = "Pickup";
    private String paymentMode = "UPI";

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHostelOrClass() {
        return hostelOrClass;
    }

    public void setHostelOrClass(String hostelOrClass) {
        this.hostelOrClass = hostelOrClass;
    }

    public String getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
