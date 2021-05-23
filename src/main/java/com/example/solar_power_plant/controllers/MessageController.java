package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.model.Message;
import com.example.solar_power_plant.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping(path = "/messages")
    public String getAllMessage(Model model) {
        model.addAttribute("messages", messageService.getAllMessages());
        return "message/messages";
    }

    @GetMapping(path = "/messages/{id}")
    public String getMessageById(@PathVariable("id") UUID id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Optional<Message> message = messageService.getMessageById(id);

        if (message.isPresent()) {
            model.addAttribute("message", message.get());
        } else {
            System.out.println(" ........ no message ........");
            redirectAttributes.addFlashAttribute("messageNotFound","Повідомлення не знайдено");
            return "redirect:/messages";
        }
        return "message/message-by-id";
    }

    @GetMapping(path = "/messages/new")
    public String getNewMessageForm(Model model) {
        model.addAttribute("message",new Message());
        return "message/new-message";
    }

    @PostMapping(path = "/messages")
    public void addMessage(@ModelAttribute("message") Message message) {
        messageService.save(message);
    }
}
