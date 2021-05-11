package com.adx.core.schedulers;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.adx.core.services.MySchedulerAnnotation;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import javax.jcr.Session;
import java.util.HashMap;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = MySchedulerAnnotation.class)
public class MyScheduler implements Runnable {

    private int schedulerId;

    Scheduler scheduler;
    ResourceResolver resourceResolver;
    ResourceResolverFactory resourceResolverFactory;
    WorkflowService workflowService;


    @Activate
    public void activate(MySchedulerAnnotation mySchedulerAnnotation)
    {
        schedulerId = 1;
        ScheduleOptions scheduleOptions = scheduler.EXPR(mySchedulerAnnotation.cronValue());

        scheduleOptions.name(mySchedulerAnnotation.schedulerName());

        scheduler.schedule(this, scheduleOptions);

    }


    @Override
    public void run() {
        try {

            String path = null;
            HashMap<String, Object> hm = new HashMap<>();
            hm.put(ResourceResolverFactory.SUBSERVICE, "mySystemUser");
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(hm);

            Session session = resourceResolver.adaptTo(Session.class);

            WorkflowSession workflowSession = workflowService.getWorkflowSession(session);
            WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/schedulerworkflow");
            WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", path);
            workflowSession.startWorkflow(workflowModel,workflowData);

        }
        catch (LoginException | WorkflowException | NullPointerException excep)
        {
            excep.printStackTrace();
        }
    }
}
