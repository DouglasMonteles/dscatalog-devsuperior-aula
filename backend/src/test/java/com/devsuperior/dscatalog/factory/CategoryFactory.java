package com.devsuperior.dscatalog.factory;

import com.devsuperior.dscatalog.entities.Category;

public class CategoryFactory {

	public static Category createCategory() {
		return new Category(1L, "Eletronics");
	}
	
}
