package com.example.solar_power_plant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

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

    public SimpleMailMessage createSimpleMail(String email, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(email);

        // TODO: 10.08.2021 Text mail.

        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        System.out.println(" 3*) ==..=.=.=.=..=.=.=.=.=.=.=.");

        return mailMessage;
    }

    public void createHTMLMail() throws MessagingException {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.gmail.com");
        sender.setPort(587);

        MimeMessage message = sender.createMimeMessage();

        // use the true flag to indicate you need a multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("ravluk2000@gmail.com");
        System.out.println("html send");

        // use the true flag to indicate the text included is HTML
        helper.setText("<html><body><h2>Header 2</h2> <p>paragraph</p></body></html>", true);

        // let's include the infamous windows Sample file (this time copied to c:/)
        /*FileSystemResource res = new FileSystemResource(new File("c:/Sample.jpg"));
        helper.addInline("identifier1234", res);*/

        sender.send(message);
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
