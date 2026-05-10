package com.example.fjm0313_takeout_self.controller.seller;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.common.context.UserContext;
import com.example.fjm0313_takeout_self.entity.Employee;
import com.example.fjm0313_takeout_self.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// 如果不是RestController而是Controller的话，会把返回的Result当页面来处理，但是Result本身并不是html文件，而变成@RestController后就可以将Result自动转成json数据传回去
@RestController
@RequestMapping("/seller/employee")
public class SellerEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // 登录
    @PostMapping("/login")
    public Result<Employee> login(@RequestBody Employee employee, HttpServletRequest request){

        Employee dbEmployee = employeeService.findByUsername(employee.getUsername());


        String md5Password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

        if(dbEmployee == null){

            return Result.fail( "未查询到用户");

        }
        if(dbEmployee.getStatus() == 0){

            return Result.fail( "账号被禁用");
        }
        if(!dbEmployee.getPassword().equals(md5Password)){

            return Result.fail( "密码错误");
        }


        // 说白了就是，登录成功后，服务端创建session(locker#ABC123,内部存储userid=1)，设置并返回cookie（JSESSIONID=ABC123），然后等张三来查的时候，通过cookie查到张三的id，然后拿着这个id去数据库查数据
        // getSession()操作就是服务端创建session，返回cookie的，是服务端自动完成的，
        request.getSession().setAttribute("employeeId",dbEmployee.getId());

        // 为什么要设置密码为null，不怕数据库里的密码也被设置成null了吗：
        // 不怕，因为这是一个副本，修改的只是Java对象
        dbEmployee.setPassword(null);

        return Result.success(dbEmployee);

    }

    // 登出
    @LoginRequired("EMPLOYEE")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employeeId");
        return Result.success("退出成功");
    }

    // 获取当前员工信息
    @LoginRequired("EMPLOYEE")
    @PostMapping("/current")
    public Result<Employee> currentEmployee(){
        Long employeeId = UserContext.getEmployeeId();
        Employee employee = employeeService.findById(employeeId);

        if(employee != null){
            employee.setPassword(null);
        }
        return Result.success(employee);

    }

}
