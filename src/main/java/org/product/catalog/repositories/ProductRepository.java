package org.product.catalog.repositories;

import org.product.catalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Transactional(readOnly = true)
    @Query("SELECT DISTINCT obj FROM Product obj WHERE UPPER(obj.name) = UPPER(:name) ")
    Optional<Product> findByName(@Param("name") String name);

    @Transactional(readOnly = true)
    @Query("SELECT p FROM Product p JOIN p.category c WHERE UPPER(c.name) = UPPER(:name)")
    List<Product> findByCategoryName(@Param("name") String name);
}
