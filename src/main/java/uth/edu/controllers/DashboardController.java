package uth.edu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard";
    }
    
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }
}
