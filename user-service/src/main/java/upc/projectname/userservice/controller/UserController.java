package upc.projectname.userservice.controller;


import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.User;
import upc.projectname.userservice.mapper.UserMapper;
import upc.projectname.userservice.service.IUserService;
import upc.projectname.userservice.utils.JwtUtils;
import upc.projectname.userservice.utils.MailCheckUtils;
import upc.projectname.userservice.utils.RandomStringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
//@RequestMapping("/user")
public class UserController {

    @Autowired
    MailCheckUtils mailCheckUtils;
    @Autowired
    IUserService userService;



    @Autowired
    UserMapper userMapper;




    @PostMapping("/getUsersByIds")
    public Result<List<User>> getUsersByIds(@RequestParam("ids") List<Integer> ids) {
        List<User> userList=userService.getUsersByIds(ids);
        return Result.success(userList);
        }

    @GetMapping("/updateUserBalance")
    public Result updateUserBalance(Double money,Integer id) {
        boolean success=userService.updateUserBalance(money,id);
        if(success) {
            return Result.success();
        }
        return  Result.error("fail to update user balance");
    }


    @GetMapping("/registerCheckCode")
    public Result getCheckCode(String toMail)
    {

        System.out.println(toMail);
        String randomString= RandomStringUtils.generateRandomString(5);
        boolean codeSend= mailCheckUtils.sendHtmlEmail("快客验证码",randomString,toMail);
        if (!codeSend)
        {
            return Result.error("邮件发送失败");
        }

        Map<String,Object> claims=new HashMap<>();
        claims.put("check",randomString);
        String check= JwtUtils.createLongTimeJwt(claims);


        log.info("验证码是 "+randomString);



        return Result.success(check);
    }
    @PostMapping("register")
    public Result register(String checkCode,String username,String password,String email,@RequestHeader("check") String checkHeader)
    {


       if (checkCode==null || checkCode.isEmpty())
       {
           return Result.error("验证码错误");
       }

        Claims claims = JwtUtils.parseJWT(checkHeader);
       if (claims==null)
       {
           return Result.error("验证码已经失效");
       }
       
        String check=claims.get("check",String.class);  

        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
//        https://ui-avatars.com/api/?name=%E6%9C%8D%E5%88%9B%E6%BC%94%E7%A4%BA&background=455a64&color=ffffff
        String before= "https://ui-avatars.com/api/?name=";
        String after="&background=455a64&color=ffffff";
        user.setImageUrl(before+username+after);
        System.out.println(checkCode);
        System.out.println(check);
        if (check.equals(checkCode))
        {
            boolean ok=userService.register(user);
            if (ok)
            {
                return Result.success();
            }
            else
            {
                return Result.error("邮箱重复");
            }
        }
        else
        {
            return Result.error("验证码错误");
        }

    }

    @PostMapping("/login")
    public Result login(String email,String password )
    {

        User user=new User();
       user.setEmail(email);
        user.setPassword(password);
         User user1=userService.login(user);
         if (user1!=null)
         {
             Map<String,Object> claims=new HashMap<>();
             claims.put("userid",user1.getUserid());
             claims.put("password",user1.getPassword());
             claims.put("username",user1.getUsername());
             claims.put("email",user1.getEmail());
             claims.put("phone",user1.getPhone());
             claims.put("nickname",user1.getNickName());
             claims.put("balance",user1.getBalance());
             claims.put("score",user1.getScore());
             claims.put("deliverReward",user1.getDeliverReward());
             claims.put("imageUrl",user1.getImageUrl());
             claims.put("sentence",user1.getSentence());
             claims.put("state",user1.getState());
             claims.put("openid",user1.getOpenid());
             claims.put("ticket",user1.getTicket());

             String jwt= JwtUtils.createLongTimeJwt(claims);
             String state=Long.toString(user1.getUserid());
             Result result = new Result(1,state,jwt);
             return result;
         }
        return  Result.error("邮箱或密码错误");
    }




    @DeleteMapping("/delete")
    public Result deleteUser( Integer userid,@RequestHeader("token") String jwt) {
        Claims claims = JwtUtils.parseJWT(jwt);
        assert claims != null;
        userid = claims.get("userid", Integer.class);
        boolean success = userService.deleteUserById(userid);
        if (success) {
            return Result.success();
        } else {
            return Result.error("Failed to delete user");
        }
    }

    @DeleteMapping("/managerDelete")
    public Result managerDeleteUser(Integer userid)
    {
        boolean success = userService.deleteUserById(userid);
        if (success) {
            return Result.success();
        } else {
            return Result.error("Failed to delete user");
        }

    }



    @PutMapping("/update")
    public Result updateUser(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        if (success) {
            return Result.success(user);
        } else {
            return Result.error("Failed to update user");
        }
    }

    @GetMapping("/get")
    public Result<User> getUserById( Integer userid) {
        User user = userService.getUserById(userid);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("Failed to find the user");
        }
    }

    @GetMapping("/managerGet")
    public Result managerGetUser(Integer userid) {
        User user = userService.getUserById(userid);
        if (user != null) {
            //查询用户state
            User user1= userMapper.selectById(userid);
            String state=Integer.toString(user1.getState());
            Result result = new Result(1, state, user);
            return result;
//            return Result.success(user);
        } else {
            return Result.error("Failed to find the user");
        }
    }


    @GetMapping("/list")
    public Result getAllUsers(Integer pageNum, Integer pageSize) {
        List<User> users = userService.getAllUsers(pageNum,pageSize);
        if (users != null) {
            return Result.success(users);
        } else {
            return Result.error("Failed to load users");
        }
    }
    @GetMapping("/listDeliveryUser")
    public Result getAllDeliveryUsers(Integer pageNum, Integer pageSize) {
        List<User> users = userService.getAllDeliveryUsers(pageNum,pageSize);
        if (users != null) {
            return Result.success(users);
        } else {
            return Result.error("Failed to load users");
        }
    }












}
