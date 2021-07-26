package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.model.*;
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
    public String getAllMessages(Model model, @RequestParam(value = "page", defaultValue = "1") String page) {
        getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));

        Optional<User> user = getAuthorisedUser();

        if(user.isPresent() && (user.get().getLocked() || !user.get().getActivated())){
            return "redirect:/home";
        }


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

            List<String> pageNumList = messageService.getNumPagesList(user.get(), limitMessages, 1);
            int pageInt = getPage(page, pageNumList.size());
            /*try {
                 pageInt = Integer.parseInt(page);
            }catch(NumberFormatException ex) {
                //System.err.println("Invalid string in argumment");
                pageInt=1;
            }*/

            model.addAttribute("messages",
                    messageService.getMessagesByRecipientForPage(
                            user.get().getId(),
                            (pageInt - 1) * (int) limitMessages,
                            (int) limitMessages));

            //List<String> pageNumList = messageService.getNumPagesList(user.get(), limitMessages,1);

            System.out.println(" - current page: "+pageInt);

            model.addAttribute("numPages", pageNumList);
            model.addAttribute("currentPage", String.valueOf(pageInt));

            //model.addAttribute("sentMessages", messageService.getAllMessageBySender(user.get()));

            /*model.addAttribute("sentMessages",messageService.getAllMessageByUserAndMessageType(
                    user.get(),
                    MessageType.FOR_EDITOR));*/
        }

        if (user.isPresent() && user.get().getUserRole()==UserRoles.ADMIN) {
            model.addAttribute("adminAccess", "admin");
            System.out.println("admin access");
        }

        return "message/messages";
    }

    @GetMapping(path = "/messages/sent")
    public String getAllSentMessages(Model model, @RequestParam(value = "page", defaultValue = "1") String page) {
        getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));

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
            //model.addAttribute("sentMessages", messageService.getAllMessageBySender(user.get()));
            List<String> pageNumList = messageService.getNumPagesList(user.get(), limitMessages, 2);

            int pageInt = getPage(page, pageNumList.size());

            model.addAttribute("sentMessages",
                    messageService.getMessagesBySenderForPage(
                            user.get().getId(),
                            (pageInt - 1) * (int) limitMessages,
                            (int) limitMessages));

            System.out.println("current page: "+pageInt);

            model.addAttribute("numPages", pageNumList);
            model.addAttribute("currentPage", String.valueOf(pageInt));

            /*model.addAttribute("sentMessages",messageService.getAllMessageByUserAndMessageType(
                    user.get(),
                    MessageType.FOR_EDITOR));*/
        }

        if (user.isPresent() && user.get().getUserRole()==UserRoles.ADMIN) {
            model.addAttribute("adminAccess", "admin");
            System.out.println("admin access");
        }

        return "message/sent-messages";
    }

    @GetMapping(path = "/messages/{id}")
    public String getMessageById(@PathVariable("id") UUID id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));

        Optional<Message> message = messageService.getMessageById(id);

        Optional<User> user=getAuthorisedUser();

        if (user.isPresent() && user.get().getUserRole()==UserRoles.ADMIN) {
            model.addAttribute("adminAccess", "admin");
            System.out.println("admin access");
        }

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
        getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));

        model.addAttribute("message", new Message());

        if (getAuthorisedUser().isPresent() && getAuthorisedUser().get().getUserRole() == UserRoles.EDITOR) {
            model.addAttribute("editorAccess", "editor");
        }

        Optional<User> user=getAuthorisedUser();

        if (user.isPresent() && user.get().getUserRole()==UserRoles.ADMIN) {
            model.addAttribute("adminAccess", "admin");
            System.out.println("admin access");
        }

        return "message/new-message";
    }

    @GetMapping(path = "/messages/getUsersList")
    public String getUserList(Model model) {
        getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));

        model.addAttribute("users", usersService.getAllUsers());
        System.out.println("user list");

        Optional<User> user=getAuthorisedUser();

        if (user.isPresent() && user.get().getUserRole()==UserRoles.ADMIN) {
            model.addAttribute("adminAccess", "admin");
            System.out.println("admin access");
        }

        return "message/user-list";
    }

    @PostMapping(path = "/messages")
    public String addMessage(@ModelAttribute("message") Message message,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(name = "type", required = false) String type,
                             @RequestParam(name = "username", required = false) String username) {
        Optional<User> user = getAuthorisedUser();
        //Optional<User> editor = usersService.getUserByUserRole(UserRoles.EDITOR);

        /*String title = message.getTitle(),
                text = message.getText();*/

        System.out.println("Message title: " + message.getTitle() + ", \n" +
                "text: " + message.getText() + " \n" +
                "type: " + type + "\n");

        String title = message.getTitle(),
                text = message.getText().trim();

        Message message1;

        if (user.isPresent() && user.get().getUserRole() == UserRoles.EDITOR) {

//            String title = message.getTitle(),
//                    text = message.getText();
//
//            Message message1;
            //List<Message> messages=new ArrayList<>();

            if (type.equals("FOR_USER")) {
                message1 = new Message();

                message1.setTitle(title);
                message1.setText(text);

                user.ifPresent(message1::setSender);

                Optional<User> recipient = usersService.getUserByUsername(username);
                if (recipient.isPresent()) {
                    message1.setRecipient(recipient.get());
                }

                message1.setRead(false);

                System.out.println(" messageType: " + type);
                //message.setMessageType(MessageType.INFORMATION);
                message1.setMessageType(MessageType.valueOf(type));
                message1.setDateTime(LocalDateTime.now());

                messageService.save(message1);
            } else {

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

                    errorMessage.setTitle("Помилка надсилання повідомлення.");
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

        redirectAttributes.addFlashAttribute("messageSent", "Повідомлення успішно надіслано.");
        return "redirect:/messages";
    }

    @PostMapping(path = "/messages/answer")
    public String answerMessage(RedirectAttributes redirectAttributes,
                                @RequestParam(name = "id") String id,
                                @RequestParam(name = "text") String text) {
        Optional<User> user = getAuthorisedUser();

        Optional<Message> message = messageService.getMessageById(UUID.fromString(id));
        if (user.isPresent() && message.isPresent()) {
            Message answer = new Message();

            answer.setTitle(message.get().getTitle());
            answer.setText(text.trim());

            answer.setSender(user.get());
            answer.setRecipient(message.get().getSender());

            answer.setRead(false);

            //String typeMessage=message.get().getMessageType().name();
            User messageSender = message.get().getSender();
            /*if (messageSender.getUserRole() == UserRoles.EDITOR) {
                answer.setMessageType(MessageType.FOR_EDITOR);
            } else if (messageSender.getUserRole() == UserRoles.ADMIN) {
                answer.setMessageType(MessageType.FOR_ADMIN);
            } else if (messageSender.getUserRole() == UserRoles.USER) {
                answer.setMessageType(MessageType.FOR_USER);
            }*/

            answer.setMessageType(MessageType.valueOf("FOR_"+messageSender.getUserRole().name()));

            answer.setDateTime(LocalDateTime.now());

            messageService.save(answer);

            redirectAttributes.addFlashAttribute("messageSent", "Повідомлення успішно надіслано.");

        } else {
            redirectAttributes.addFlashAttribute("messageNotSent", "Повідомлення не надіслано.");
        }

        return "redirect:/messages";
    }

    /*@DeleteMapping(path = "/messages/{id}/delete")
    public String deleteMessageById(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        //usersService.deleteUserById(Long.valueOf(id));

        Optional<Message> message = messageService.getMessageById(UUID.fromString(id));


        if (message.isPresent() && getAuthorisedUser().isPresent()) {
            messageService.deleteMessage(message.get());

            //Тут можна надіслати ласта користувачу про видалення його аккаунта

            redirectAttributes.addFlashAttribute("deleteMessage", "Повідомлення успішно видалено.");
        } else {
            redirectAttributes.addFlashAttribute("deleteMessage", "Сталась помилка, спробуйте пізніше.");
        }
        //model.addAttribute("messages", messageService.getAllSolarPowerPlants());

        return "redirect:/messages";
    }*/

    @GetMapping(path = "/message-alert1")
    public String getMessageAlert(Model model){
        getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages1",
                messageService.getCountUnreadMessagesByUser(user)));

        System.out.println(".... message-alert1");
        return "dashboard/user/message-alert1";
    }

    @GetMapping(path = "/getTest")
    public String getTestG(){
        return "gt";
    }

    Optional<User> getAuthorisedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        return usersService.getUserByUsername(username);
    }

    private int getPage(String page, int maxPage) {
        int pageInt;
        try {
            pageInt = Integer.parseInt(page);
        } catch (NumberFormatException ex) {
            //System.err.println("Invalid string in argumment");
            pageInt = 1;
        }

        if (pageInt > maxPage) pageInt = 1;

        return pageInt;
    }
}
