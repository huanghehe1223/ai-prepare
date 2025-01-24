package upc.projectname.projectservice.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import upc.projectname.upccommon.api.client.StudentClient;

@RestController
public class ProjectController {

    @Autowired
    private StudentClient studentClient;

    @GetMapping("/test")
    public String test() {
        String student = studentClient.getStudent();
        return "project-service: " + student;
    }
}
