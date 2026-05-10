package com.example.fjm0313_takeout_self.controller.customer;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.common.context.UserContext;
import com.example.fjm0313_takeout_self.entity.AddressBook;
import com.example.fjm0313_takeout_self.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/address")
public class CustomerAddressController {

    @Autowired
    private AddressBookService addressBookService;

    @LoginRequired("CUSTOMER")
    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBook addressBook){
        Long userId = UserContext.getUserId();
        addressBook.setUserId(userId);
        addressBookService.addAddress(addressBook);
        return Result.success(addressBook);
    }

    @LoginRequired("CUSTOMER")
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook){
        Long userId = UserContext.getUserId();
        addressBookService.updateAddress(addressBook);
        return Result.success("修改成功");

    }

    @LoginRequired("CUSTOMER")
    @DeleteMapping
    public Result<String> delete(@RequestBody AddressBook addressBook){
        Long userId = UserContext.getUserId();
        addressBookService.deleteAddress(addressBook.getId());
        return Result.success("删除成功");
    }

    @LoginRequired("CUSTOMER")
    @GetMapping("/list")
    public Result<List<AddressBook>> list( ){
        Long userId = UserContext.getUserId();
        List<AddressBook> list = addressBookService.findByUserId(userId);
        return Result.success(list);
    }

    @LoginRequired("CUSTOMER")
    @GetMapping("/{id}")
    public Result<AddressBook> get(@PathVariable Long id){
        Long userId = UserContext.getUserId();
        AddressBook addressBook = addressBookService.findById(id);
        return Result.success(addressBook);
    }


    @LoginRequired("CUSTOMER")
    @PutMapping("/default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        Long userId = UserContext.getUserId();

        addressBookService.clearDefaultByUserId(userId);
        addressBook.setIsDefault(1);
        addressBookService.updateAddress(addressBook);
        return Result.success(addressBook);
    }

    @LoginRequired("CUSTOMER")
    @GetMapping("/default")
    public Result<AddressBook> getDefault( ){
        Long userId = UserContext.getUserId();
        AddressBook addressBook = addressBookService.findDefaultByUserId(userId);
        if(addressBook == null){
            return Result.fail("没有默认地址");
        }
        return Result.success(addressBook);
    }




}
