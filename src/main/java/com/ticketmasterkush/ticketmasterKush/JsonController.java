package com.ticketmasterkush.ticketmasterKush;

import com.ticketmasterkush.ticketmasterKush.database.Task;
import com.ticketmasterkush.ticketmasterKush.database.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("")
public class JsonController {

    private S3Client s3Client;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    JsonController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @GetMapping("")
    public String gethome() {
        return "home";
    }

    @GetMapping("/task")
    public List<Task> getTask(){
        List<Task> all = (List)taskRepository.findAll();
        return all;
    }

    @PostMapping("/task")
    public List<Task> postTask(@RequestBody Task task) {
        if(task.getAssignee().trim().isEmpty()){
            task.setStatus("Available");
        } else {
            task.setStatus("Assigned");
        }
        taskRepository.save(task);
        List<Task> all = (List)taskRepository.findAll();
        return all;
    }

    @PutMapping("/tasks/{id}/state")
    public Task putTask(@PathVariable UUID id) {
        Task t = taskRepository.findById(id).get();
        if(t.getStatus().equals("Available")){
          t.setStatus("Assigned");
        } else if(t.getStatus().equals("Assigned")){
            t.setStatus("Accepted");
        } else if(t.getStatus().equals("Accepted")){
            t.setStatus("Finished");
        } else if(t.getStatus()== null) {
            t.setStatus("Available");
        }
        taskRepository.save(t);
        return t;
    }

    @GetMapping("/users/{name}/tasks")
    public List<Task> taskWithName(@PathVariable String name){
        List<Task> t = taskRepository.findByAssignee(name);
        return t;
    }

    @PutMapping("/tasks/{id}/assign/{assignee}")
    public Task putTask(@PathVariable UUID id, @PathVariable String assignee) {
        Task t = taskRepository.findById(id).get();
        t.setAssignee(assignee);
        t.setStatus("Assigned");
        taskRepository.save(t);
        return t;
    }

    @PostMapping("/task/{id}/images")
    public RedirectView uploadFile(@PathVariable UUID id, @RequestPart(value = "file") MultipartFile file){
        Task t = taskRepository.findById(id).get();
        ArrayList<String> list = this.s3Client.uploadFile(file);
        t.setPic(list.get(0));
        t.setResized(list.get(1));
        taskRepository.save(t);
        return new RedirectView("http://taskmaster-react-frontend.s3-website.us-east-2.amazonaws.com");
//        return new RedirectView("http://localhost:3000/");

    }

    @GetMapping("/tasks/{id}")
    public Task uploadFile(@PathVariable UUID id){
        Task t = taskRepository.findById(id).get();
        return t;
    }
}
