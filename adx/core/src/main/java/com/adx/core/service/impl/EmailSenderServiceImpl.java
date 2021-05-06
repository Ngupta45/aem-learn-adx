package com.adx.core.service.impl;

import com.adx.core.service.EmailSenderService;
import com.adobe.acs.commons.email.EmailService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = EmailSenderService.class,immediate = true)
public class EmailSenderServiceImpl  implements EmailSenderService{

    private Logger LOGGER = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

    @Reference
    EmailService emailService;

    @Override
    public void sendMail(List<String> nonRepPAges)
    {
        try{
            String templatePath = "/etc/notification/email/default/emailTemplate.txt";
            HashMap<String,String> params = new HashMap<>();
            params.put("msg",nonRepPAges.toString());

            String[] mailTo = { "mansi.19972@gmail.com","pushpender661@gmail.com" };
            List<String> failureList = emailService.sendEmail(templatePath, params, mailTo);
            if (failureList.isEmpty()) {
                LOGGER.info("Email sent successfully to the recipients");
            } else {
                LOGGER.info("Email sent failed");
            }
        }
        catch(Exception e){
            LOGGER.info("[EmailSenderSericeImpl] Exception Occured : ",e);
        }

    }

}
