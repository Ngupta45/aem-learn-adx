package com.adx.core.schedulers;

import com.adx.core.services.NonReplicatedPagesMailerService;;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Collections;
import java.util.Map;

@Component( service = Runnable.class, immediate = true)
public class NonReplicatedPagesMailerScheduler implements Runnable {

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    @Reference
    protected WorkflowService workflowService;

    @Reference
    protected Scheduler scheduler;

    @Reference
    protected NonReplicatedPagesMailerService nonReplicatedPagesMailerService;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String WORKFLOW_MODEL_PATH = "/var/workflow/models/NonReplicatedPagesMailerWorkflow";

    private String PAYLOAD;

    private static final String SERVICE_NAME = "workflow-user";

    private static final Map<String, Object> USER_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);

    private static final String SCHEDULER_ID = "NonReplicatedPagesMailerScheduler";

    @Activate
    protected void activate() {
        PAYLOAD = nonReplicatedPagesMailerService.getPayload();
        ScheduleOptions scheduleOptions = scheduler.EXPR(nonReplicatedPagesMailerService.getCron());
        scheduleOptions.name(SCHEDULER_ID);
        scheduleOptions.canRunConcurrently(true);
        if (nonReplicatedPagesMailerService.isActive()) {
            scheduler.schedule(this, scheduleOptions);
        } else {
            scheduler.unschedule(SCHEDULER_ID);
        }
    }

    @Override
    public void run() {
        ResourceResolver resourceResolver = null;
        Session session = null;
        WorkflowSession workflowSession = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(USER_INFO);
            session = resourceResolver.adaptTo(Session.class);
            workflowSession = workflowService.getWorkflowSession(session);

            WorkflowModel workflowModel = workflowSession.getModel(WORKFLOW_MODEL_PATH);
            WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", PAYLOAD);
            workflowSession.startWorkflow(workflowModel, workflowData);

        } catch (LoginException | WorkflowException e) {
            LOG.info("Exception Occurred : {}", e.getMessage(), e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
            if (session != null) {
                session.logout();
            }
            if (workflowSession != null) {
                workflowSession.logout();
            }
        }
    }
}
