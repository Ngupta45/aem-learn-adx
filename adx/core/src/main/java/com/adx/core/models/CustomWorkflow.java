package com.mysite.core.models;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.mysite.core.services.ReplicatedPages;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service=WorkflowProcess.class,
        property = { "process.label" + "=Prakhar Workflow"}
)
public class CustomWorkflow implements WorkflowProcess
{
    private static final Logger log = LoggerFactory.getLogger(CustomWorkflow.class);

    @Reference
    ReplicatedPages replicatedPages;
    MessageGateway<Email> messageGateway;
    private MessageGatewayService messageGatewayService;

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


                Email email = new SimpleEmail();
                String emailto="prakharkhare52.rk@gmail.com";

                    email.addTo(emailto);
                    email.setSubject("Page that are not replicated");
                    email.setMsg(String.valueOf(replicatedPages.NonReplicated()));
                    email.setFrom("prakharkhare52.rk@gmail.com");
                    messageGateway=messageGatewayService.getGateway(Email.class);
                    messageGateway.send((Email) email);





            }



        } catch (Exception e)
        {
            e.printStackTrace();
            log.error("\n error {}",e);
        }

    }

}

