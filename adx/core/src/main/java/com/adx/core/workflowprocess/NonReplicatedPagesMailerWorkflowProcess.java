package com.adx.core.workflowprocess;


import com.adx.core.services.Mailer;
import com.adx.core.services.NonReplicatedPageFinder;
import com.adx.core.services.NonReplicatedPagesMailerService;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label= Non Replicated Pages Finder and Mailer",
                "service.description = Finds non replicated pages and e-mail them",
                "service.vendor = ADX"
        }
)
public class NonReplicatedPagesMailerWorkflowProcess implements WorkflowProcess {

    @Reference
    protected NonReplicatedPageFinder nonReplicatedPageFinder;

    @Reference
    protected Mailer mailer;

    @Reference
    protected NonReplicatedPagesMailerService nonReplicatedPagesMailerService;

    private String[] recipients;
    private boolean isActivate;

    @Activate
    protected void init() {
        recipients = nonReplicatedPagesMailerService.getRecipientsMailID();
        isActivate = nonReplicatedPagesMailerService.isActive();
    }

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        WorkflowData workflowData = workItem.getWorkflowData();
        if (isActivate) {
            mailer.sendmail(recipients, nonReplicatedPageFinder.getNonReplicatedPages(workflowData.getPayload().toString()), workflowData.getPayload().toString());
        }
        else {
            String[] processArgs = metaDataMap.get("PROCESS_ARGS", String.class).split(",");
            if (processArgs.length > 0) {
                mailer.sendmail(processArgs, nonReplicatedPageFinder.getNonReplicatedPages(workflowData.getPayload().toString()), workflowData.getPayload().toString());
            }
        }
    }
}
