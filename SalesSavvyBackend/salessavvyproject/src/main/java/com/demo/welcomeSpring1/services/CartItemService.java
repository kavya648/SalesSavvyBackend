package com.demo.welcomeSpring1.services;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.welcomeSpring1.entities.CartItem;
import com.demo.welcomeSpring1.entities.Product;
import com.demo.welcomeSpring1.entities.ProductImage;
import com.demo.welcomeSpring1.entities.User;
import com.demo.welcomeSpring1.repositories.CartRepository;
import com.demo.welcomeSpring1.repositories.ProductImageRepository;
import com.demo.welcomeSpring1.repositories.ProductRepository;
import com.demo.welcomeSpring1.repositories.UserRepository;

@Service
public class CartItemService {
    @Autowired
    CartRepository cartItemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    public CartItemService(CartRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    public int getCartItemCount(int userId) {

        return cartItemRepository.countTotalItems(userId);

    }
    public void addToCart(int userId, int productId, int quantity) {
         User user = userRepository.findById(userId).orElse(null);
         Product product = productRepository.findById(productId).orElse(null);
         Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(userId, productId);
         if(existingItem.isPresent()) {
        	 CartItem cartItem = existingItem.get();
        	 cartItem.setQuantity(quantity);
        	 cartItemRepository.save(cartItem);

         }
         else
         {

         CartItem cartItem = new CartItem(user, product, quantity);
         cartItemRepository.save(cartItem);
    }
   }

    public Map<String, Object> getCartItems(int userId) {
    	List <CartItem > cartItems = cartItemRepository.findCartItemsWithProductDetails(userId);
    	Map <String, Object> response = new HashMap<>();
    	User user = userRepository.findById(userId).orElse(null);
    	response.put("username", user.getUsername());
    	response.put("role", user.getRole());
    	List <Map<String,Object>> products = new ArrayList<>();
    	int overallTotalPrice = 0;

    	for(CartItem cartItem : cartItems) {
    		Map<String, Object> productDetails = new HashMap<>();
    		Product product = cartItem.getProduct();
    		List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(product.getProductId());
    		String imageUrl = null;

    		if(productImages!= null && !productImages.isEmpty()) {
    			imageUrl = productImages.get(0).getImageUrl();
    		}
    		else
    		{
    			imageUrl = "image not found";
    		}
    		productDetails.put("product_id",  product.getProductId());
    		productDetails.put("image_url",  imageUrl);
    		productDetails.put("name",  product.getName());
    		productDetails.put("description", product.getDescription());
    		productDetails.put("price_per_unit", product.getPrice());
    		productDetails.put("quantity", cartItem.getQuantity());
    		double totalprice = cartItem.getQuantity()*product.getPrice().doubleValue();
    		productDetails.put("total_price", totalprice);


    		products.add(productDetails);

    		overallTotalPrice += cartItem.getQuantity()*product.getPrice().doubleValue();

    	}

    	Map<String, Object> cart = new HashMap<>();
    	cart.put("products",products);
    	cart.put("overall_total_price", overallTotalPrice);
    	response.put("cart", cart);


    	return response;
    }
    public void updateCartItemQuantity(int userId, int productId, int quantity) {
    	User user = userRepository.findById(userId)
    			.orElseThrow(() -> new IllegalArgumentException("User not found"));

    	Product product = productRepository.findById(productId)
    			.orElseThrow(() -> new IllegalArgumentException("Product not found"));

    	//Fetch cart item for this userId and productId
    	Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(userId, productId);

    	if(existingItem.isPresent()) {
    		CartItem cartItem = existingItem.get();
    		if(quantity == 0) {
    			deleteCartItem(userId, productId);
    		}
    		else
    		{
    			cartItem.setQuantity(quantity);
    			cartItemRepository.save(cartItem);
    		}
    	}

    }


    //Delete Cart Item
    public void deleteCartItem(int userId, int productId) {
    	User user = userRepository.findById(userId)
    			.orElseThrow(() -> new IllegalArgumentException("User not found"));

    	Product product = productRepository.findById(productId)
    			.orElseThrow(() -> new IllegalArgumentException("Product not found"));

    	cartItemRepository.deleteCartItem(userId, productId);

    }
}
