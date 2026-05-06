package com.example.fjm0313_takeout_self.controller.customer;


import com.example.fjm0313_takeout_self.common.LoginRequired;
import com.example.fjm0313_takeout_self.common.Result;
import com.example.fjm0313_takeout_self.common.UserContext;
import com.example.fjm0313_takeout_self.entity.User;
import com.example.fjm0313_takeout_self.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer/user")
public class CustomerUserController  {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user){
        // 查重名
        String username = user.getUsername();



        User existUser = userService.findByUsername(username);

        if(existUser != null){
            return Result.fail("用户名已被占用，请更换！");
        }
        // 加密密码

        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        // 启用

        user.setStatus(1);
        // 插入数据库

        userService.register(user);

        return Result.success("用户注册成功！");
    }


    @PostMapping("/login")
    public Result<User> login(@RequestBody User user, HttpServletRequest request){
        String username = user.getUsername();
        User exitUser = userService.findByUsername(username);
        if(exitUser == null){
            return Result.fail("用户名错误");
        }
        String md5passWord = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());

        if(!exitUser.getPassword().equals(md5passWord)){
            return  Result.fail("密码错误");
        }
        if(exitUser.getStatus() == 0){
            return  Result.fail("用户已被禁用");
        }


        request.getSession().setAttribute("userId",exitUser.getId());
        user.setPassword(null);
        return Result.success(user);

    }

    @LoginRequired("CUSTOMER")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("userId");
        return Result.success("退出成功");

    }

    @LoginRequired("CUSTOMER")
    @PostMapping("/current")
    public Result<User> currentUser(){
        Long userId = UserContext.getUserId();

        User user = userService.findById(userId);
        user.setPassword(null);
        return Result.success(user);
    }



}
