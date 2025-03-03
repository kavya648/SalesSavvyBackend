package com.demo.welcomeSpring1.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.demo.welcomeSpring1.entities.OrderItem;
import com.demo.welcomeSpring1.entities.Product;
import com.demo.welcomeSpring1.entities.ProductImage;
import com.demo.welcomeSpring1.entities.User;
import com.demo.welcomeSpring1.repositories.OrderItemRepository;
import com.demo.welcomeSpring1.repositories.OrderRepository;
import com.demo.welcomeSpring1.repositories.ProductImageRepository;
import com.demo.welcomeSpring1.repositories.ProductRepository;

@Service
public class OrderService {

	OrderRepository orderRepository;


	OrderItemRepository orderItemRepository;


	ProductRepository productRepository;


	ProductImageRepository productImageRepository;


	public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, ProductImageRepository productImageRepository) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
	}

	public Map<String, Object> getOrdersForUser(User user) {

		List<OrderItem> orderItems = orderItemRepository.findSuccessfulOrderItemsByUserId(user.getUserId());


				Map<String, Object> response = new HashMap<>();

		        response.put("username", user.getUsername());
		        response.put("role", user.getRole());


		        List<Map<String, Object>> products = new ArrayList<>();
		        for (OrderItem item: orderItems) {

		        	Product product = productRepository.findById(item.getProductId()).orElse(null);
		        	if(product == null) {
		        		continue;

		        	}

		        	List<ProductImage> images = productImageRepository.findByProduct_ProductId(product.getProductId());
		        	String imageUrl = images.isEmpty()?null:images.get(0).getImageUrl();

		        	Map<String, Object> productDetails = new HashMap<>();
		        	productDetails.put("order_id", item.getOrder().getOrderId());
		        	productDetails.put("quantity",item.getQuantity());

		        	productDetails.put("total_price", item.getTotalPrice());

		        	productDetails.put("imageUrl",imageUrl);

		        	productDetails.put("product_id", product.getProductId());
		        	productDetails.put("name", product.getName());
		        	productDetails.put("description", product.getDescription());
		        	productDetails.put("price_per_unit", item.getPricePerUnit());

		        	products.add(productDetails);

		        }
		        response.put("products", products);


		      return response;
	}

}
