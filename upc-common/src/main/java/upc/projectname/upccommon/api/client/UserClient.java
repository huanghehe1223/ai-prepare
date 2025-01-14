package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.User;

import java.util.Collection;
import java.util.List;

@FeignClient("user-service")
public interface UserClient {

    @GetMapping("/get")
    Result<User> getUserById(@RequestParam("userid") Integer userid);

    @GetMapping("/updateUserBalance")
    Result updateUserBalance(@RequestParam("money") Double money,@RequestParam("id") Integer id);

    @PostMapping(value = "/getUsersByIds", consumes = "application/x-www-form-urlencoded")
    Result<List<User>> getUsersByIds(@RequestParam("ids") Collection<Integer> ids);

}
