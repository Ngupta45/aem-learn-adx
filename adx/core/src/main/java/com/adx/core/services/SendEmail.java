package com.adx.core.services;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.LoginException;
import org.osgi.service.component.annotations.Component;
import com.adx.core.services.ReplicatedPagesService;

import com.adobe.acs.commons.email.EmailService;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;


@Component(service = WorkflowProcess.class , immediate = true ,property = {
        "process.label = EmailSend WorkFlow"
})
public class SendEmail implements  WorkflowProcess{

    @Reference
    ReplicatedPagesService replicatedPagesService;

    @Reference
    EmailService emailService;

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
    {
        try {
            log.info("-----------------Worfrkflow start----------------");
            String path= "/content/usergroupassign";
            String templatePath = "/etc/notification/email/emailTemplate.txt";
            HashMap<String,String> hm = new HashMap<>();
            hm.put("path",path);
            hm.put("message",String.valueOf(replicatedPagesService.getNonReplicatedPages()));
            String[] receivers = {"mansi.19972@gmail.com"};
            List<String> list = emailService.sendEmail(templatePath, hm, receivers);
            log.info("-----------------Worfrkflow end----------------");
        }
        catch(Exception excep)
        {
            log.info(" my error{}",excep.getMessage(),excep);
        }

    }
}
