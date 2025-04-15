package com.linh.ecommerce.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

  private final PaymentService service;
  private final PayOS payOS;
  /**
   * Tạo một thanh toán mới
   */
  @PostMapping
  public ResponseEntity<String> createPayment(
      @RequestBody @Valid PaymentRequest request
  ) throws Exception {
    log.info("Creating payment: {}", request);
    return ResponseEntity.ok(service.createPayment(request));
  }
  
  /**
   * Lấy trạng thái thanh toán
   */
//  @GetMapping("/payments/status/{orderReference}")
//  public ResponseEntity<PaymentResponse> getPaymentStatus(
//      @PathVariable String orderReference
//  ) {
//    log.info("Getting payment status for order reference: {}", orderReference);
////    return ResponseEntity.ok(service.getPaymentStatus(orderReference));
//      return null;
//  }
  
  /**
   * Webhook để nhận thông báo từ PayOS
   */

  @PostMapping(path = "/confirm-webhook")
  public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode response = objectMapper.createObjectNode();
    try {
      String str = payOS.confirmWebhook(requestBody.get("webhookUrl"));
      response.set("data", objectMapper.valueToTree(str));
      response.put("error", 0);
      response.put("message", "ok");
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      response.put("error", -1);
      response.put("message", e.getMessage());
      response.set("data", null);
      return response;
    }
  }

//<<<<===================================== >>>>



//  @PostMapping("/payments/webhook")
//  public ResponseEntity<String> handleWebhook(
//      HttpServletRequest request,
//      @RequestBody PayOSWebhookRequest webhookRequest
//  ) {
//    log.info("Received webhook from PayOS: {}", webhookRequest);
//
//    // Lấy tất cả headers
//    Map<String, String> headers = new HashMap<>();
//    Enumeration<String> headerNames = request.getHeaderNames();
//    while (headerNames.hasMoreElements()) {
//      String headerName = headerNames.nextElement();
//      headers.put(headerName.toLowerCase(), request.getHeader(headerName));
//    }
//
//    // Xử lý webhook
////    service.handlePayOSWebhook(headers, webhookRequest);
//
//    return ResponseEntity.ok("OK");
//  }
}
