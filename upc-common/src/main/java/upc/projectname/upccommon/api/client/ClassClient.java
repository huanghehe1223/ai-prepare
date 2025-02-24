package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Class;

import java.util.List;

@FeignClient(name = "user-class-service", contextId = "classClient")
public interface ClassClient {

    @GetMapping("/class/{id}")
    Result<Class> getClass(@PathVariable Integer id);

    @PostMapping("/class/batch")
    Result<List<Class>> getClassByIds(@RequestBody List<Integer> ids);

    @PostMapping("/class")
    Result<Boolean> saveClass(@RequestBody Class clazz);

    @PutMapping("/class")
    Result<Boolean> updateClass(@RequestBody Class clazz);

    @DeleteMapping("/class/{id}")
    Result<Boolean> deleteClass(@PathVariable Integer id);
} 