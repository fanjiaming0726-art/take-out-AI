package com.example.fjm0313_takeout_self.controller.customer;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.common.context.UserContext;
import com.example.fjm0313_takeout_self.entity.ShoppingCart;
import com.example.fjm0313_takeout_self.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/customer/shoppingCart")
public class CustomerCartController {

    @Autowired
    private ShoppingCartService cartService;

    // 添加
    @LoginRequired("CUSTOMER")
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart cart){
        // 找出用户的订单，找有没有这道菜，有就把数量＋1，没有就新加
        Long userId = UserContext.getUserId();
        cart.setUserId(userId);
        ShoppingCart existCart = cartService.findExistItem(userId,cart.getDishId(),cart.getFlavor(),cart.getPortion());

        if(existCart != null){
            existCart.setNumber(existCart.getNumber() + 1);
            cartService.updateCartItem(existCart);
            return Result.success(existCart);
        }else{
            cart.setNumber(1);
            cartService.addCartItem(cart);
            return Result.success(cart);
        }
    }

    @LoginRequired("CUSTOMER")
    @PostMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody ShoppingCart cart){
        Long userId = UserContext.getUserId();
        ShoppingCart existCart = cartService.findExistItem(userId,cart.getDishId(),cart.getFlavor(),cart.getPortion());
        if(existCart == null){
            return Result.fail("购物单不存在");

        }
        int number = existCart.getNumber() - 1;

        if(number > 0){
            existCart.setNumber(number);
            cartService.updateCartItem(existCart);
            return Result.success(existCart);
        }else{
            cartService.removeCartItem(existCart.getId());
            return Result.success(null);
        }


    }

    @LoginRequired("CUSTOMER")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        Long userId = UserContext.getUserId();
        List<ShoppingCart> list = cartService.findByUserId(userId);

        return Result.success(list);
    }

    @LoginRequired("CUSTOMER")
    @DeleteMapping("/clean")
    public Result<String> clean(){
        Long userId = UserContext.getUserId();
        cartService.clearByUserId(userId);

        return Result.success("清空成功");

    }

    
}
