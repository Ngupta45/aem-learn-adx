package com.adx.core.listeners;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.acs.commons.email.EmailService;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
//import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.List;

@Component(service = WorkflowProcess.class,immediate = true, property = {
        "process.label = Email Workflow Assignment"
})
public class CustomProcessForEmail implements WorkflowProcess
{
    @Reference
    protected NonReplicatedPages nonReplicatedPageFinder;

    @Reference
    private MessageGatewayService messageGatewayService;

    @Reference
    EmailService emailService;

    public Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {

        String pagePath = "/content/adx";
        String templatePath = "/etc/notification/email/emailTemplate.txt";

        HashMap<String, String> emailParams = new HashMap<>();
        emailParams.put("pagePath",pagePath);
        emailParams.put("message",String.valueOf(nonReplicatedPageFinder.getNonReplicatedPages()));
        String[] recipients = {"goyalsakshi2398@gmail.com"};
        List<String> failureList=emailService.sendEmail(templatePath,emailParams,recipients);
        if (failureList.isEmpty()) {
            log.info("Email sent successfully to the recipients");
        } else {
            log.info("Email sent failed");
        }


    }
}

