package com.leyou.cart.web;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping("/cart")
    public ResponseEntity<Void> add(Cart cart){
        cartService.add(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/cart/list")
    public ResponseEntity<List<Cart>> getCartList(){
        return ResponseEntity.ok(cartService.getCartList());
    }
    @PutMapping
    public ResponseEntity<Void> putCart(@RequestParam("skuId") Long skuId,@RequestParam("num") int num){
        cartService.putCart(skuId,num);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
