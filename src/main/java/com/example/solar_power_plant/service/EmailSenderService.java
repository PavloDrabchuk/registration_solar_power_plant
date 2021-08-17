package com.example.solar_power_plant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:project.properties")
public class EmailSenderService {



    private final JavaMailSender javaMailSender;

    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        System.out.println("4) ==..=.=.=.=..=.=.=.=.=.=.=.");

        javaMailSender.send(email);
        System.out.println("5) ==..=.=.=.=..=.=.=.=.=.=.=.");

        System.out.println("send... send ... send");
    }

    public void sendEmailWithSubjectAndText(String email, String subject, String text) {
        SimpleMailMessage confirmationMessage = new SimpleMailMessage();
        confirmationMessage.setTo(email);

        // TODO: 10.08.2021 Text mail.

        confirmationMessage.setSubject(subject);
        confirmationMessage.setText(text);
        System.out.println("3) ==..=.=.=.=..=.=.=.=.=.=.=.");

        sendEmail(confirmationMessage);
    }

    /*public void sendRemovingAccountEmail(String email) {
        System.out.println("2) ==..=.=.=.=..=.=.=.=.=.=.=.");

        String subject = "Видалення аккаунту";
        String text = "Доброго дня. Ваш аккаунт видалено з системи. У разі виникнення питань звертайтесь до адміністратора:"
                + ADMIN_EMAIL + ".";

        sendEmailWithSubjectAndText(email, subject, text);
    }

    public void sendRemovingSolarPowerPlantEmail(String email) {
        System.out.println("1) ==..=.=.=.=..=.=.=.=.=.=.=.");
        String subject = "Видалення сонячної електростанції";
        String text = "Доброго дня. Вашу сонячну електростанцію видалено з системи. У разі виникнення питань звертайтесь до адміністратора:"
                + ADMIN_EMAIL + ".";

        sendEmailWithSubjectAndText(email, subject, text);
    }*/

}
