package com.adx.core.services;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.LoginException;
import org.osgi.service.component.annotations.Component;
import com.adx.core.services.impli.MyMidnightImpli;
import org.osgi.service.component.annotations.Reference;
import com.adobe.acs.commons.email.EmailService;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = WorkflowProcess.class , immediate = true ,property = {
        "process.label = Email Confirmation Workflow"
})

public class Email implements WorkflowProcess {
    Logger logger = LoggerFactory.getLogger(Email.class);
    @Reference
    MyMidnightImpli myMidnightProcessImpli;

    EmailService emailService;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
    {

            String path= "/content/weretail";
            String templatePath = "/etc/notification/email/template.txt";
            HashMap<String,String> hm = new HashMap<>();
            hm.put("path",path);
            hm.put("message",String.valueOf(myMidnightProcessImpli.getNonReplicatedPages()));
            String[] receivers = {"himanshujiit23@gmail.com"};
            List<String> list = emailService.sendEmail(templatePath, hm, receivers);



    }
}


