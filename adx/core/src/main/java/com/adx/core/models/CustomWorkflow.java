package com.adx.core.models;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adx.core.Services.ReplicatedPages;
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
                log.info("pages are {}", replicatedPages.NonReplicated());
                String path= "/content/mysite";
                String templatePath = "/etc/notification/email/acs-commons/emailtemplate.txt";
                Map<String,String> hm = new HashMap<String,String>();
                hm.put("message",String.valueOf(replicatedPages.NonReplicated()));
                hm.put("path",path);
                hm.put("body","list of non-replicated pages are");
                hm.put("senderName","Prakhar Khare");
                String[] receivers = {"prakharkhare52.rk@gmail.com"};
                List<String> list = emailService.sendEmail(templatePath, hm, receivers);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("\n error {}",e);
        }
    }

}

