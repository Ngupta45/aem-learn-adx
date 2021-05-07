package com.adx.core.schedulers;

import com.adx.core.config.WorkflowSchedulerConfig;
import com.adx.core.utils.ResolverUtil;
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
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = WorkflowSchedulerConfig.class)
public class MyWorkflowScheduler implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MyWorkflowScheduler.class);
    private int schedulerId;
    private ResourceResolver resourceResolver;

    @Reference
    Scheduler scheduler;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    WorkflowService workflowService;

    @Activate
    public void activate(WorkflowSchedulerConfig workflowSchedulerConfig) {
        schedulerId = workflowSchedulerConfig.schName().hashCode();
        ScheduleOptions scheduleOptions = scheduler.EXPR(workflowSchedulerConfig.cronValue());
        scheduleOptions.name(String.valueOf(schedulerId));
//        scheduleOptions.canRunConcurrently(false);
        scheduler.schedule(this, scheduleOptions);
//        ScheduleOptions scheduleOptionsNow = scheduler.NOW(1, 3);//number of executions
//        scheduler.schedule(this, scheduleOptionsNow);
        //TO Execute immediately
    }

    @Deactivate
    public void deactivate(WorkflowSchedulerConfig workflowSchedulerConfig) {
        scheduler.unschedule(String.valueOf(schedulerId));
    }

    @Override
    public void run() {
        LOG.info("\n -----------------EXECUTING RUN METHOD SCHEDULER MY WORKFLOW -------------------");
        try {
            resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
            String payload = "/content/aemgeeks_example/us/en";
            String status = "Workflow Executing";
            Session session = resourceResolver.adaptTo(Session.class);
            WorkflowSession workflowSession = workflowService.getWorkflowSession(session);

            WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/geeks-page-version");
            WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", payload);
            status = workflowSession.startWorkflow(workflowModel, workflowData).getState();
            LOG.info("\n --------------------------" + status + "----------------------");
        } catch (LoginException | WorkflowException | NullPointerException e) {
            e.printStackTrace();
            LOG.error("\n error in workflow execution  message : - {} error :  - {} " + e.getMessage() + e);
        }
    }
}
