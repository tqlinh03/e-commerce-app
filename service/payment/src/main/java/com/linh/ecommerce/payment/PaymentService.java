package com.linh.ecommerce.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

  private final PaymentRepository repository;
  private final PaymentMapper mapper;
  private final PayOS payOS;

  /**
   * Tạo link thanh toán PayOS
   */
  @Transactional
  public String createPaymentLink(PaymentRequest request) throws Exception {
    PaymentData paymentData = PaymentData
            .builder()
            .orderCode(request.orderReference())
            .amount((request.amount()).intValue())
            .description("Thanh toán đơn hàng")
            .returnUrl(request.returnUrl())
            .cancelUrl(request.cancelUrl())
            .build();

    CheckoutResponseData result = payOS.createPaymentLink(paymentData);
    return result.getCheckoutUrl();
  }

  /**
   * Tạo thanh toán mới
   */
  @Transactional
  public String createPayment(PaymentRequest request) throws Exception {
    // Tạo thanh toán trong hệ thống
    var payment = mapper.toPayment(request);

    // Lưu thanh toán vào database
    repository.save(payment);
    
    // Tạo link thanh toán
    return this.createPaymentLink(request);
  }



  
  /**
   * Xử lý webhook từ PayOS
   */
//  @Transactional
//  public void handlePayOSWebhook(Map<String, String> headers, PayOSWebhookRequest webhookRequest) {
//    // Xác minh chữ ký webhook
//    boolean signatureValid = payOSClient.verifyWebhookSignature(headers, webhookRequest.toString());
//    if (!signatureValid) {
//      log.error("Invalid webhook signature");
//      throw new PaymentException("Invalid webhook signature");
//    }
//
//    // Lấy mã đơn hàng từ webhook
//    String orderCode = webhookRequest.getData().getOrderCode();
//
//    // Tìm thanh toán tương ứng
//    Optional<Payment> optionalPayment = repository.findByOrderReference(orderCode);
//    if (optionalPayment.isEmpty()) {
//      log.error("Payment not found for order reference: {}", orderCode);
//      throw new PaymentException("Payment not found for order reference: " + orderCode);
//    }
//
//    Payment payment = optionalPayment.get();
//
//    // Cập nhật trạng thái thanh toán
//    // Giả sử mã thành công là "00"
//    if ("00".equals(webhookRequest.getData().getCode())) {
//      payment.setStatus(PaymentStatus.COMPLETED);
//    } else {
//      payment.setStatus(PaymentStatus.FAILED);
//    }
//
//    // Lưu cập nhật
//    repository.save(payment);
//
//    log.info("Payment updated: {}", payment.getId());
//  }
  
  /**
   * Kiểm tra trạng thái thanh toán
   */
//  public PaymentResponse getPaymentStatus(String orderReference) {
//    Optional<Payment> optionalPayment = repository.findByOrderReference(orderReference);
//    if (optionalPayment.isEmpty()) {
//      throw new PaymentException("Payment not found for order reference: " + orderReference);
//    }
//
//    return mapper.toPaymentResponse(optionalPayment.get());
//  }
  }
