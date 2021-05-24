package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.model.Message;
import com.example.solar_power_plant.model.MessageType;
import com.example.solar_power_plant.model.User;
import com.example.solar_power_plant.model.UserRoles;
import com.example.solar_power_plant.service.MessageService;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MessageController {

    private final MessageService messageService;
    private final UsersService usersService;

    @Autowired
    public MessageController(MessageService messageService,
                             UsersService usersService) {
        this.messageService = messageService;
        this.usersService = usersService;
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
            redirectAttributes.addFlashAttribute("messageNotFound", "Повідомлення не знайдено");
            return "redirect:/messages";
        }
        return "message/message-by-id";
    }

    @GetMapping(path = "/messages/new")
    public String getNewMessageForm(Model model) {
        model.addAttribute("message", new Message());

        if (getAuthorisedUser().isPresent() && getAuthorisedUser().get().getUserRole() == UserRoles.EDITOR) {
            model.addAttribute("editorAccess", "editor");
        }

        return "message/new-message";
    }

    @PostMapping(path = "/messages")
    public String addMessage(@ModelAttribute("message") Message message,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(name = "type") String type) {
        Optional<User> user = getAuthorisedUser();
        Optional<User> editor = usersService.getUserByUserRole(UserRoles.EDITOR);

        user.ifPresent(message::setUser);
        editor.ifPresent(message::setEditor);

        message.setRead(false);

        System.out.println(" messageType: "+type);
        //message.setMessageType(MessageType.INFORMATION);
        message.setMessageType(MessageType.valueOf(type));
        message.setDateTime(LocalDateTime.now());

        messageService.save(message);

        redirectAttributes.addFlashAttribute("messageSent", "Повідомлення надіслано");
        return "redirect:/messages";
    }

    Optional<User> getAuthorisedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        return usersService.getUserByUsername(username);
    }
}
