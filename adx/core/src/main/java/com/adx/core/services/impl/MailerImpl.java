package com.adx.core.services.impl;

import com.adx.core.services.Mailer;

import com.adobe.acs.commons.email.EmailService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = Mailer.class, immediate = true)
public class MailerImpl implements Mailer {

    @Reference
    protected EmailService emailService;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public void sendmail(String[] recipient, List<String> content, String pagePath) {
        String templatePath = "/etc/notification/email/emailTemplate.txt";

        Map<String, String> emailParams = new HashMap<>();
        emailParams.put("pagePath", pagePath);
        emailParams.put("message", String.valueOf(content));

        List<String> failureList = emailService.sendEmail(templatePath, emailParams, recipient);
        if (failureList.isEmpty()) {
            LOG.info("Email sent successfully to the recipients");
        } else {
            LOG.info("Email sent failed");
        }
    }
}
