package com.adx.core.schedulers;

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

import javax.jcr.Session;
import java.util.*;

@Component(service = Runnable.class,immediate = true)
@Designate(ocd = MidNightSchedulerConfig.class)
public class MidNightScheduler implements Runnable {


    private int schedulerId;

    @Reference
    private Scheduler scheduler;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    WorkflowService workflowService;

    private String schedulerName;
    private String[] emailId;

    @Activate
    public void activate(MidNightSchedulerConfig conf)
    {
        schedulerId = conf.schdulerName().hashCode();
        emailId = conf.getEmailID();
        schedulerName = conf.schdulerName();
        ScheduleOptions scheduleOptions = scheduler.EXPR(conf.getCronExpression());
        scheduleOptions.name(conf.schdulerName());
        scheduler.schedule(this,scheduleOptions);
        ScheduleOptions options = scheduler.NOW(1,3);
        scheduler.schedule(this,options);
    }

    @Deactivate
    protected void deactivate(){
        scheduler.unschedule(schedulerName);
    }

    public String[] getEmail(){
        return emailId;
    }

    @Override
    public void run(){
        try{
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put(ResourceResolverFactory.SUBSERVICE,"sysUser");
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(hashMap);
            Session session = resourceResolver.adaptTo(Session.class);

            WorkflowSession workflowSession = workflowService.getWorkflowSession(session);
            String payload = "/content/adx";
            WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/Email_Scheduler");
            WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH",payload);
            workflowSession.startWorkflow(workflowModel,workflowData);

        }
        catch(LoginException | WorkflowException e)
        {
            e.printStackTrace();
        }
    }

}
