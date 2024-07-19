package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.model.User;
import com.example.todolist.repository.TaskRepository;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listTasks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        List<Task> tasks = taskRepository.findByUserId(user.getId());
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/new")
    public String showTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "task_form";
    }

    @PostMapping
    public String createTask(@AuthenticationPrincipal UserDetails userDetails, Task task) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        task.setUser(user);
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable("id") Long id, Model model) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        model.addAttribute("task", task);
        return "task_form";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable("id") Long id, Task task) {
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        taskRepository.delete(task);
        return "redirect:/tasks";
    }

    @GetMapping("/search")
    public String searchTasks(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("keyword") String keyword, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        List<Task> tasks = taskRepository.findByUserIdAndTitleContainingOrDescriptionContaining(user.getId(), keyword, keyword);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
}
