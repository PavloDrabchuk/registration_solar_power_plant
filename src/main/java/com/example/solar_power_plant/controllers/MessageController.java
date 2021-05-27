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
import java.util.ArrayList;
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
    public String getAllMessage(Model model, @RequestParam(value = "page", defaultValue = "1") String page) {
        Optional<User> user = getAuthorisedUser();
        if (user.isPresent()) {
            double limitMessages = 4;
            //List<Message> messages = messageService.getAllMessageByUser(user.get());

            /*List<Message> messages = messageService.getAllMessageByMessageType(MessageType.INFORMATION);
            messages.addAll(messageService.getAllMessageByMessageType(MessageType.UPDATE));
            messages.addAll(messageService.getAllMessageByMessageType(MessageType.ERROR));

            if (user.get().getUserRole()==UserRoles.EDITOR){
                messages.addAll(messageService.getAllMessageByMessageType(MessageType.FOR_EDITOR));

                model.addAttribute("sentMessages",messageService.getAllMessageByMessageType(
                        MessageType.FOR_USER));
            }else {
                model.addAttribute("sentMessages",messageService.getAllMessageByUserAndMessageType(
                        user.get(),
                        MessageType.FOR_EDITOR));
            }

            model.addAttribute("messages", messages);*/

            model.addAttribute("messages", messageService.getAllMessageByRecipient(user.get()));
            model.addAttribute("sentMessages", messageService.getAllMessageBySender(user.get()));

            /*model.addAttribute("sentMessages",messageService.getAllMessageByUserAndMessageType(
                    user.get(),
                    MessageType.FOR_EDITOR));*/
        }
        return "message/messages";
    }

    @GetMapping(path = "/messages/sent")
    public String getAllSentMessage(Model model, @RequestParam(value = "page", defaultValue = "1") String page) {
        Optional<User> user = getAuthorisedUser();
        if (user.isPresent()) {
            double limitMessages = 4;
            //List<Message> messages = messageService.getAllMessageByUser(user.get());

            /*List<Message> messages = messageService.getAllMessageByMessageType(MessageType.INFORMATION);
            messages.addAll(messageService.getAllMessageByMessageType(MessageType.UPDATE));
            messages.addAll(messageService.getAllMessageByMessageType(MessageType.ERROR));

            if (user.get().getUserRole()==UserRoles.EDITOR){
                messages.addAll(messageService.getAllMessageByMessageType(MessageType.FOR_EDITOR));

                model.addAttribute("sentMessages",messageService.getAllMessageByMessageType(
                        MessageType.FOR_USER));
            }else {
                model.addAttribute("sentMessages",messageService.getAllMessageByUserAndMessageType(
                        user.get(),
                        MessageType.FOR_EDITOR));
            }

            model.addAttribute("messages", messages);*/

            //model.addAttribute("messages", messageService.getAllMessageByRecipient(user.get()));
            model.addAttribute("sentMessages", messageService.getAllMessageBySender(user.get()));

            /*model.addAttribute("sentMessages",messageService.getAllMessageByUserAndMessageType(
                    user.get(),
                    MessageType.FOR_EDITOR));*/
        }
        return "message/sent-messages";
    }

    @GetMapping(path = "/messages/{id}")
    public String getMessageById(@PathVariable("id") UUID id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Optional<Message> message = messageService.getMessageById(id);

        if (message.isPresent()) {
            model.addAttribute("message", message.get());
            message.get().setRead(true);

            messageService.save(message.get());
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
                             @RequestParam(name = "type", defaultValue = "ERROR") String type) {
        Optional<User> user = getAuthorisedUser();
        //Optional<User> editor = usersService.getUserByUserRole(UserRoles.EDITOR);

        /*String title = message.getTitle(),
                text = message.getText();*/

        String title = message.getTitle(),
                text = message.getText();

        Message message1;

        if (user.isPresent() && user.get().getUserRole() == UserRoles.EDITOR) {

//            String title = message.getTitle(),
//                    text = message.getText();
//
//            Message message1;
            //List<Message> messages=new ArrayList<>();

            for (User user1 : usersService.getAllUsers()) {
                message1 = new Message();

                message1.setTitle(title);
                message1.setText(text);

                user.ifPresent(message1::setSender);
                message1.setRecipient(user1);
                message1.setRead(false);

                System.out.println(" messageType: " + type);
                //message.setMessageType(MessageType.INFORMATION);
                message1.setMessageType(MessageType.valueOf(type));
                message1.setDateTime(LocalDateTime.now());

                messageService.save(message1);
            }
        } else {
            switch (type) {
                case "FOR_EDITOR": {
                    for (User editor : usersService.getAllUsersByUserRole(UserRoles.EDITOR)) {
                        message1 = new Message();

                        message1.setTitle(title);
                        message1.setText(text);

                        user.ifPresent(message1::setSender);
                        message1.setRecipient(editor);
                        message1.setRead(false);

                        message1.setMessageType(MessageType.valueOf(type));
                        message1.setDateTime(LocalDateTime.now());

                        messageService.save(message1);
                    }
                    break;
                }
                case "FOR_ADMIN": {
                    for (User admin : usersService.getAllUsersByUserRole(UserRoles.ADMIN)) {
                        message1 = new Message();

                        message1.setTitle(title);
                        message1.setText(text);

                        user.ifPresent(message1::setSender);
                        message1.setRecipient(admin);
                        message1.setRead(false);

                        message1.setMessageType(MessageType.valueOf(type));
                        message1.setDateTime(LocalDateTime.now());

                        messageService.save(message1);
                    }
                    break;
                }
                default: {
                    System.out.println("error message");

                    Optional<User> editor = usersService.getUserByUserRole(UserRoles.EDITOR);

                    Message errorMessage = new Message();

                    errorMessage.setTitle("Помилка надсилання");
                    errorMessage.setText("Під час надсилання вашого повідомлення сталась помилка. " +
                            "Спробуйте повторити Ваші дії трохи пізніше. Якщо проблему не буде вирішено, " +
                            "то відправте повідомлення до технічної підтримки на адресу електронної пошти: " +
                            "solar.power.plant.system@gmail.com. <br><br>" +
                            "Вибачте за незручності.");

                    editor.ifPresent(errorMessage::setSender);
                    user.ifPresent(errorMessage::setRecipient);
                    errorMessage.setRead(false);

                    errorMessage.setMessageType(MessageType.ERROR);
                    errorMessage.setDateTime(LocalDateTime.now());

                    messageService.save(errorMessage);

                    break;
                }
            }
        }
        //user.ifPresent(message::setSender);
        //editor.ifPresent(message::setRecipient);

        /*message.setRead(false);

        System.out.println(" messageType: " + type);
        //message.setMessageType(MessageType.INFORMATION);
        message.setMessageType(MessageType.valueOf(type));
        message.setDateTime(LocalDateTime.now());*/

//        messageService.save(message);

        redirectAttributes.addFlashAttribute("messageSent", "Повідомлення надіслано");
        return "redirect:/messages";
    }

    Optional<User> getAuthorisedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        return usersService.getUserByUsername(username);
    }
}
