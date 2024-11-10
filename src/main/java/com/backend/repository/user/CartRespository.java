package com.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.backend.entity.Cart;
import java.util.List;

import com.backend.entity.Product;
import com.backend.entity.User;
import com.backend.entity.Sku;

@Repository
public interface CartRespository extends JpaRepository<Cart, Long>, JpaSpecificationExecutor<Cart>  {
	Cart findOneCartByUserAndProductAndSku(User user, Product product, Sku sku);

}
