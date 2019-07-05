package com.ticketmasterkush.ticketmasterKush;

import com.ticketmasterkush.ticketmasterKush.database.Task;
import com.ticketmasterkush.ticketmasterKush.database.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class JsonController {

    @Autowired
    TaskRepository taskRepository;

    @GetMapping("/")
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

    @PutMapping("/tasks/{id}/state/{assignee}")
    public Task putTask(@PathVariable UUID id, @PathVariable String assignee) {
        Task t = taskRepository.findById(id).get();
        t.setAssignee(assignee);
        t.setStatus("Assigned");
        taskRepository.save(t);
        return t;
    }
}
