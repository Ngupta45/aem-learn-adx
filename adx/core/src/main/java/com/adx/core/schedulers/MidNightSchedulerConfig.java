package com.adx.core.schedulers;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name="MidNight Scheduler",description = "MidNight WorkFlow Scheduler configuration"
)
public @interface MidNightSchedulerConfig {

    @AttributeDefinition(
            name = "Scheduler Name",
            description = "Scheduler Name",
            type = AttributeType.STRING)
    public String schdulerName() default "MidNight WorkFlow Scheduler Configuration";

    @AttributeDefinition(
            name = "Scheduler Email",
            description = "Enter Email",
            type = AttributeType.STRING)
    public String[] getEmailID() default "tester@argildx.com";


    @AttributeDefinition(
            name = "Cron Expression",
            description = "Cron expression to be used by scheduler",
            type = AttributeType.STRING)
    public String getCronExpression() default "0 0/1 * 1/1 * ? *";
    // for midnight   "0 0 0 1/1 * ? *"
}
