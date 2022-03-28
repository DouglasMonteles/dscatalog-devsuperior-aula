package com.devsuperior.dscatalog.factory;

import java.time.Instant;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class ProductFactory {

	public static Product createProduct() {
		var product = new Product(1L, "Phone", "Good Phone", 800.0, 
				"image.png", Instant.parse("2022-03-17T03:00:00Z"));
		product.getCategories().add(new Category(1L, "Eletronics"));
		
		return product; 
	}
	
	public static ProductDTO createProductDTO() {
		var product = createProduct();
		var productDto = new ProductDTO(product, product.getCategories());
		
		return productDto;
	}
	
}
