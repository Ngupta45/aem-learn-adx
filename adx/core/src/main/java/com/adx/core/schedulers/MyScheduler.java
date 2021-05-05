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
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = MyScheduler.MyConfig.class)

public class MyScheduler implements  Runnable{
    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    WorkflowService workflowService;

    @Reference
    Scheduler scheduler;

    @ObjectClassDefinition(
            name ="Workflow Scheduler",
            description = "Scheduler Assignment"
    )
    public @interface MyConfig{
        @AttributeDefinition(
                name = "Scheduler Name",
                description = "Enter Scheduler Name",
                type = AttributeType.STRING
        )
        public String getSchedulerName() default "EmailWorkFlowScheduler";

        @AttributeDefinition(
                name = "Scheduler Email",
                description = "Enter Email",
                type = AttributeType.STRING
        )
        public String[] getEmail() default "himanshu.gupta@argildx.com";

        @AttributeDefinition(
                name = "cron Expression",
                description = "Enter Cron Expression",
                type = AttributeType.STRING
        )
        public String getCronExpression() default "0 0 0 1/1 * ? *";
    }
    private String schedulerName;
    private String[] email;

    @Activate
    public  void activate(MyScheduler.MyConfig myServiceConfig){
        email=myServiceConfig.getEmail();
        schedulerName = myServiceConfig.getSchedulerName();
        ScheduleOptions scheduleOptions=scheduler.EXPR(myServiceConfig.getCronExpression());
        scheduleOptions.name(myServiceConfig.getSchedulerName());
        scheduler.schedule(this,scheduleOptions);
        ScheduleOptions optionsNow=scheduler.NOW(2,5);
        scheduler.schedule(this,optionsNow);
    }

    @Deactivate
    protected void deactivate(){
        scheduler.unschedule(schedulerName);
    }

    public String[] getEmail(){
        return email;
    }

    @Override
    public void run() {
        try {
            HashMap<String, Object> hmap = new HashMap<>();
            hmap.put(ResourceResolverFactory.SUBSERVICE, "testuser");
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(hmap);
            Session session = resourceResolver.adaptTo(Session.class);
            WorkflowSession workflowSession = workflowService.getWorkflowSession(session);
            String payload="/content/we-retail";
            WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/himanshu-s-workflow");
            WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH",payload);
            workflowSession.startWorkflow(workflowModel,workflowData);
        } catch (LoginException | WorkflowException e) {
            e.printStackTrace();
        }

    }

}
