package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.enums.MessageType;
import com.example.solar_power_plant.enums.UserRoles;
import com.example.solar_power_plant.model.*;
import com.example.solar_power_plant.service.MessageService;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MessageController {

    private final MessageService messageService;
    private final UsersService usersService;

    private Optional<User> authorizedUser = Optional.empty();

    @Autowired
    public MessageController(MessageService messageService,
                             UsersService usersService) {
        this.messageService = messageService;
        this.usersService = usersService;
    }

    @GetMapping(path = "/messages")
    public String getAllMessages(Model model, @RequestParam(value = "page", defaultValue = "1") String page) {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent() && (authorizedUser.get().getLocked() || !authorizedUser.get().getActivated())) {
            return "redirect:/home";
        }

        if (authorizedUser.isPresent()) {
            double limitMessages = 4;

            int pageNumList = AuthorizationAccess.getNumPagesList(messageService.getAllMessageByRecipient(authorizedUser.get()), limitMessages);

            int pageInt = AuthorizationAccess.getPage(page, pageNumList);

            model.addAttribute("messages",
                    messageService.getMessagesByRecipientForPage(
                            authorizedUser.get().getId(),
                            (pageInt - 1) * (int) limitMessages,
                            (int) limitMessages));

            model.addAttribute("numPages", pageNumList);
            model.addAttribute("currentPage", pageInt);
        }

        return "message/messages";
    }

    @GetMapping(path = "/messages/sent")
    public String getAllSentMessages(Model model, @RequestParam(value = "page", defaultValue = "1") String page) {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent()) {
            double limitMessages = 4;

            int pageNumList = AuthorizationAccess.getNumPagesList(messageService.getAllMessageBySender(authorizedUser.get()), limitMessages);

            int pageInt = AuthorizationAccess.getPage(page, pageNumList);

            model.addAttribute("sentMessages",
                    messageService.getMessagesBySenderForPage(
                            authorizedUser.get().getId(),
                            (pageInt - 1) * (int) limitMessages,
                            (int) limitMessages));

            model.addAttribute("numPages", pageNumList);
            model.addAttribute("currentPage", pageInt);
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
            redirectAttributes.addFlashAttribute("messageNotFound", "Повідомлення не знайдено");
            return "redirect:/messages";
        }

        return "message/message-by-id";
    }

    @GetMapping(path = "/messages/sent/{id}")
    public String getSentMessageById(@PathVariable("id") UUID id,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        // TODO: 19.12.2021 У цьому методі та у попередньому об'єднай функціонал.
        Optional<Message> message = messageService.getMessageById(id);

        if (message.isPresent()) {
            model.addAttribute("message", message.get());

        } else {
            redirectAttributes.addFlashAttribute("messageNotFound", "Повідомлення не знайдено");
            return "redirect:/messages";
        }

        return "message/message-by-id";
    }

    @GetMapping(path = "/messages/new")
    public String getNewMessageForm(Model model) {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        model.addAttribute("message", new Message());

        if (authorizedUser.isPresent() && authorizedUser.get().getUserRole() == UserRoles.ROLE_EDITOR) {
            model.addAttribute("editorAccess", "editor");
        }

        return "message/new-message";
    }

    @GetMapping(path = "/messages/getUsersList")
    public String getUserList(Model model) {
        model.addAttribute("users", usersService.getAllUsers());

        return "message/user-list";
    }

    @PostMapping(path = "/messages")
    public String addMessage(@ModelAttribute("message") Message message,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(name = "type", required = false) String type,
                             @RequestParam(name = "username", required = false) String username) {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        String title = message.getTitle(),
                text = message.getText().trim();

        Message message1;

        if (authorizedUser.isPresent() && authorizedUser.get().getUserRole() == UserRoles.ROLE_EDITOR) {
            if (type.equals("FOR_ROLE_USER")) {
                Optional<User> recipient = usersService.getUserByUsername(username);

                message1 = messageService.prepareMessage(title, text, authorizedUser, recipient.orElseThrow(), type);
                messageService.save(message1);
            } else {
                for (User user1 : usersService.getAllUsers()) {
                    message1 = messageService.prepareMessage(title, text, authorizedUser, user1, type);
                    messageService.save(message1);
                }
            }
        } else {
            // TODO: 19.12.2021 Зроби функцію, яка приймає як параметр роль користувача та створює повідомлення.
            //  В цьому методі відкидай частину FOR_.
            switch (type) {
                case "FOR_ROLE_EDITOR": {
                    for (User editor : usersService.getAllUsersByUserRole(UserRoles.ROLE_EDITOR)) {

                        message1 = messageService.prepareMessage(title, text, authorizedUser, editor, type);
                        messageService.save(message1);
                    }
                    break;
                }
                case "FOR_ROLE_ADMIN": {
                    for (User admin : usersService.getAllUsersByUserRole(UserRoles.ROLE_ADMIN)) {

                        message1 = messageService.prepareMessage(title, text, authorizedUser, admin, type);
                        messageService.save(message1);
                    }
                    break;
                }
                default: {

                    Optional<User> editor = usersService.getUserByUserRole(UserRoles.ROLE_EDITOR);

                    title = "Помилка надсилання повідомлення.";
                    text = "Під час надсилання вашого повідомлення сталась помилка. " +
                            "Спробуйте повторити Ваші дії трохи пізніше. Якщо проблему не буде вирішено, " +
                            "то відправте повідомлення до технічної підтримки на адресу електронної пошти: " +
                            "solar.power.plant.system@gmail.com. <br><br>" +
                            "Вибачте за незручності.";

                    Message errorMessage = messageService.prepareMessage(title, text, editor, authorizedUser.get(), "ERROR");

                    messageService.save(errorMessage);
                    break;
                }
            }
        }

        redirectAttributes.addFlashAttribute("messageSent", "Повідомлення успішно надіслано.");
        return "redirect:/messages";
    }

    @PostMapping(path = "/messages/answer")
    public String answerMessage(RedirectAttributes redirectAttributes,
                                @RequestParam(name = "id") String id,
                                @RequestParam(name = "text") String text) {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        Optional<Message> message = messageService.getMessageById(UUID.fromString(id));

        if (authorizedUser.isPresent() && message.isPresent()) {
            Message answer = new Message();

            answer.setTitle(message.get().getTitle());
            answer.setText(text.trim());

            answer.setSender(authorizedUser.get());
            answer.setRecipient(message.get().getSender());
            answer.setRead(false);

            User messageSender = message.get().getSender();

            answer.setMessageType(MessageType.valueOf("FOR_" + messageSender.getUserRole().name()));
            answer.setDateTime(LocalDateTime.now());

            messageService.save(answer);

            redirectAttributes.addFlashAttribute("messageSent", "Повідомлення успішно надіслано.");

        } else {
            redirectAttributes.addFlashAttribute("messageNotSent", "Повідомлення не надіслано.");
        }

        return "redirect:/messages";
    }
}
