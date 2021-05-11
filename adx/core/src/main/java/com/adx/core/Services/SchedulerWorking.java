package com.adx.core.Services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Prakhar scheduler",description = "working of a scheduler"
)
public @interface SchedulerWorking
{
   @AttributeDefinition(
           name = "Scheduler name",
           type = AttributeType.STRING)
           public String schname() default "Its Prakhar Scheduler";
   @AttributeDefinition(
           name = "Scheduler Time",
           type = AttributeType.STRING)
    public String schtime() default "0 0 0 1/1 * ? *";

}
