package com.linh.ecommerce.payment;

/**
 * Trạng thái của thanh toán
 */
public enum PaymentStatus {
    PENDING,    // Đang chờ thanh toán
    COMPLETED,  // Đã thanh toán thành công
    FAILED,     // Thanh toán thất bại
    CANCELED,   // Đã hủy thanh toán
    REFUNDED,   // Đã hoàn tiền
    EXPIRED     // Hết hạn thanh toán
} 