package com.adx.core.services;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import javax.jcr.Session;
import java.util.HashMap;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = SchedularConfig.class)
public class SchedularConfigImpl implements Runnable {

    private int schId;
    protected String[] emailAddress;
    @Reference
    Scheduler scheduler;

    @Reference
    WorkflowService workflow;

    @Reference
    ResourceResolverFactory resolverFactory;

    @Activate
    protected void activate(SchedularConfig config) {

        schId = config.getSchedularName().hashCode();
        ScheduleOptions options = scheduler.EXPR(config.getCronTime());
        options.name(config.getSchedularName());
        scheduler.schedule(this, options);
        ScheduleOptions optionsNow = scheduler.NOW(3, 10);
        scheduler.schedule(this, optionsNow);
    }

    @Override
    public void run() {
        try {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put(ResourceResolverFactory.SUBSERVICE, "testServiceUser");
            ResourceResolver resourceResolver = resolverFactory.getResourceResolver(paramMap);
            Session session = resourceResolver.adaptTo(Session.class);
            WorkflowSession workflowSession = workflow.getWorkflowSession(session);
            WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/email-workflow");
            WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", "/content/slingproject/us/en");
            workflowSession.startWorkflow(workflowModel, workflowData);
        } catch (NullPointerException | LoginException | WorkflowException e) {
            e.printStackTrace();
        }
    }
}

