package com.adx.core.models;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.adx.core.Services.ReplicatedPages;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.adobe.acs.commons.email.EmailService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(
        service=WorkflowProcess.class,
        property = { "process.label" + "=Prakhar Workflow"}
)
public class CustomWorkflow implements WorkflowProcess
{
    private static final Logger log = LoggerFactory.getLogger(CustomWorkflow.class);

    @Reference
    ReplicatedPages replicatedPages;
    @Reference
    EmailService emailService;


    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException
    {
        try
        {
            WorkflowData workflowData = (WorkflowData) workItem.getWorkflowData();
            if (workflowData.getPayloadType().equals("JCR_PATH"))
            {
                //Session session=workflowSession.getSession();
                //String[] pages = new String[replicatedPages.NonReplicated().size()];
                log.info("pages are {}", replicatedPages.NonReplicated());


                String path= "/content/mysite";
                String templatePath = "/etc/notification/email/acs-commons/emailtemplate.txt";
                Map<String,String> hm = new HashMap<String,String>();
                hm.put("message",String.valueOf(replicatedPages.NonReplicated()));
                hm.put("path",path);
                String[] receivers = {"prakharkhare52.rk@gmail.com"};
                List<String> list = emailService.sendEmail(templatePath, hm, receivers);





            }



        } catch (Exception e)
        {
            e.printStackTrace();
            log.error("\n error {}",e);
        }

    }

}

