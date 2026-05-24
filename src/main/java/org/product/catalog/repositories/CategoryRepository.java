package org.product.catalog.repositories;

import org.hibernate.query.Page;
import org.product.catalog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Transactional(readOnly = true)
    @Query("SELECT DISTINCT obj FROM Category obj WHERE UPPER(obj.name) = UPPER(:name) ")
    Optional<Category> findByName(@Param("name") String name);

}
