package com.demo.welcomeSpring1.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.demo.welcomeSpring1.entities.CartItem;
import com.demo.welcomeSpring1.entities.Order;
import com.demo.welcomeSpring1.entities.OrderItem;
import com.demo.welcomeSpring1.entities.OrderStatus;
import com.demo.welcomeSpring1.repositories.CartRepository;
import com.demo.welcomeSpring1.repositories.OrderItemRepository;
import com.demo.welcomeSpring1.repositories.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {



@Value("${razorpay.key_id}")
private String razorpayKeyId;

@Value("${razorpay.key_secret}")
private String razorpayKeySecret;


private final OrderRepository orderRepository;


private final OrderItemRepository orderItemRepository;


private final CartRepository cartRepository;

public PaymentService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartRepository cartRepository) {
	this.orderRepository = orderRepository;
	this.orderItemRepository = orderItemRepository;
	this.cartRepository = cartRepository;
}


//STEP 1 GENERATE RAZORPAY ORDER
//step -1A ..... instantiate RazorPayClient using keyid and secretKey
public String createOrder(int userId, BigDecimal totalAmount, List<OrderItem> cartItems) throws RazorpayException {
    // Create Razorpay client
    RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    // step 1B create the order request which should contain amount, currency type and receipt.
    // Prepare Razorpay order request
    var orderRequest = new JSONObject();
    orderRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
    orderRequest.put("currency", "INR");
    orderRequest.put("receipt", "txn_" + System.currentTimeMillis());


    //step 1c create razor pay order using order request created in JSON object format
    // Create Razorpay order
    com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);


   // step -2: store the order in DB
    // Save order details in the database
    Order order = new Order();
    order.setOrderId(razorpayOrder.get("id"));
    order.setUserId(userId);
    order.setTotalAmount(totalAmount);
    order.setStatus(OrderStatus.PENDING);
    order.setCreatedAt(LocalDateTime.now());
    orderRepository.save(order);


   // step -3: return  order id created for making payment
    return razorpayOrder.get("id");
}

@Transactional
public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature, int userId) {
    try {
        // Prepare signature validation attributes
        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", razorpayOrderId);
        attributes.put("razorpay_payment_id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);

        // Verify Razorpay signature
        boolean isSignatureValid = com.razorpay.Utils.verifyPaymentSignature(attributes, razorpayKeySecret);

        if (isSignatureValid) {
            // Update order status to SUCCESS
            Order order = orderRepository.findById(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
            order.setStatus(OrderStatus.SUCCESS);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            // Fetch cart items for the user
            List<CartItem> cartItems = cartRepository.findCartItemsWithProductDetails(userId);

            // Save order items
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProductId(cartItem.getProduct().getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPricePerUnit(cartItem.getProduct().getPrice());
                orderItem.setTotalPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                orderItemRepository.save(orderItem);
            }

            // Clear user's cart
            cartRepository.deleteAllCartItemsByUserId(userId);
            return true;
        } else {
            return false;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

@Transactional
public void saveOrderItems(String orderId, List<OrderItem> items) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found"));
    for (OrderItem item : items) {
        item.setOrder(order);
        orderItemRepository.save(item);
    }
}
}

