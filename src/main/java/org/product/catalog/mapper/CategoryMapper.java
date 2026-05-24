package org.product.catalog.mapper;

import org.product.catalog.dto.CategoryDTO;
import org.product.catalog.model.Category;

public class CategoryMapper {
    public static Category toModel(CategoryDTO categoryDTO) {
        Category category = new Category();

        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());

        return category;
    }

    public static CategoryDTO toDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setId(category.getId());
        String name = category.getName().trim();
        if (!name.isEmpty()) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
        categoryDTO.setName(name);

        return categoryDTO;
    }
}
