package com.demo.welcomeSpring1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demo.welcomeSpring1.entities.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
List<Product> findByCategory_CategoryId(Integer categoryId);



@Query("SELECT p.category.categoryName FROM Product p WHERE p.productId = :productId")
String findCategoryNameByProductId( int productId);
}




