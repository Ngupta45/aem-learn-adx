package com.mysite.core.schedulers;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.mysite.core.services.SchedulerWorking;
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
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true,service = Runnable.class)
@Designate(ocd = SchedulerWorking.class)

public class FirstSceduler implements Runnable
{
    private static final Logger logger= LoggerFactory.getLogger(FirstSceduler.class);
    private int schid;
    private ResourceResolver resourceResolver;

    @Reference
    Scheduler scheduler;
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    @Reference
    WorkflowService workflowService;
    @Activate
    public void activate(SchedulerWorking schedulerWorking)
    {
       schid=schedulerWorking.schname().hashCode();
        ScheduleOptions scheduleOptions=scheduler.EXPR(schedulerWorking.schtime());
        scheduleOptions.name(String.valueOf(schid));
        scheduler.schedule(this,scheduleOptions);
        ScheduleOptions scheduleOptions1=scheduler.NOW(1,2);//how many times scheduler will executes
        scheduler.schedule(this,scheduleOptions1);



    }
    @Deactivate
    public void deactivate(SchedulerWorking schedulerWorking)
    {
        scheduler.unschedule(String.valueOf(schid));
    }
    @Override
    public void run()
    {
        try
        {
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "user-test1");
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
            String payload="/content/mysite/us/en/prakhar";
            Session session=resourceResolver.adaptTo(Session.class);
            WorkflowSession workflowSession=workflowService.getWorkflowSession(session);
            WorkflowModel workflowModel=workflowSession.getModel("/var/workflow/models/PageReplication");
            WorkflowData workflowData=workflowSession.newWorkflowData("JCR_PATH",payload);
            workflowSession.startWorkflow(workflowModel,workflowData);
        } catch (LoginException | WorkflowException e) {
            e.printStackTrace();
        }

    }
}
