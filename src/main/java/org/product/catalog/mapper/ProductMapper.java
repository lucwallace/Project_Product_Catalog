package org.product.catalog.mapper;

import org.product.catalog.dto.ProductDTO;
import org.product.catalog.model.Product;

import java.util.Base64;

public class ProductMapper {
    public static Product toModel(ProductDTO productDTO) {
        Product product = new Product();

        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setUrl(productDTO.getUrl());

        if (productDTO.getCategoryDTO() != null) {
            product.setCategory(CategoryMapper.toModel(productDTO.getCategoryDTO()));
        }

        if (productDTO.getPhoto() != null) {
            product.setPhoto(Base64.getDecoder().decode(productDTO.getPhoto()));
        }
        return product;
    }

    public static ProductDTO toDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setId(product.getId());
        String name = product.getName().trim();
        if (!name.isEmpty()) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
        productDTO.setName(name);
        productDTO.setPrice(product.getPrice());
        productDTO.setUrl(product.getUrl());

        if (product.getCategory() != null) {
            productDTO.setCategoryDTO(CategoryMapper.toDTO(product.getCategory()));
        }

        if (product.getPhoto() != null) {
            String base64 = Base64.getEncoder().encodeToString(product.getPhoto());

            productDTO.setPhoto(base64);
        }

        return productDTO;
    }
}
