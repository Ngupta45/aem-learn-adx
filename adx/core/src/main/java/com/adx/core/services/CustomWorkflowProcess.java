package com.adx.core.services;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.sling.api.resource.LoginException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component(service = WorkflowProcess.class,
        immediate = true,
        property = {"process.label" + "=JCR non-Replicated Pages Workflow Process"})
public class CustomWorkflowProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(CustomWorkflowProcess.class);

    @Reference
    NonReplicatedPages nonReplicatedPages;

    @Reference
    MessageGatewayService messageGatewayService;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        WorkflowData workflowData = workItem.getWorkflowData();
        StringBuffer allNonReplicatedPages = new StringBuffer();
        String[] arguments = metaDataMap.get("PROCESS_ARGS", "string").toString().split(",");


        if (((WorkflowData) workflowData).getPayloadType().equals("JCR_PATH")) {
            String path = workflowData.getPayload().toString() + "jcr:content";
            try {
                log.info("==================inside try CustomWorkflowProcess============================");
                String[] pageArray = new String[nonReplicatedPages.getAllNonReplicatedPages().size()];
                for (String page : nonReplicatedPages.getAllNonReplicatedPages())
                    allNonReplicatedPages.append(page + ", ");
                log.info("String of all Non-Replicated pages= {}", allNonReplicatedPages);
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
        try {
            MessageGateway<Email> messageGateway;
            Email email = new SimpleEmail();
            if (arguments.length > 0) {
                for (int i = 0; i < arguments.length; i++)
                    email.addTo(arguments[i]);
            }
            email.setSubject("Workflow email test");
            email.setFrom("anjali.prasad9701@gmail.com");
            email.setMsg(allNonReplicatedPages.toString());

            messageGateway = messageGatewayService.getGateway(Email.class);
            messageGateway.send(email);
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}


