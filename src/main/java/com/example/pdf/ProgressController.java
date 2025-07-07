package com.example.pdf;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ProgressController {
    private final SimpMessagingTemplate template;

    public ProgressController(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendProgress(String taskId, int percent) {
        template.convertAndSend("/topic/progress/" + taskId, percent);
    }
}