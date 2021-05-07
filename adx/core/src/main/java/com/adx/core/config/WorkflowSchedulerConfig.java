package com.adx.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Chetan Workflow Scheduler",
        description = "Workflow Scheduler Configuration"
)
public @interface WorkflowSchedulerConfig {
    @AttributeDefinition(
            name = "Scheduler Name",
            type = AttributeType.STRING)
    public String schName() default "Default Scheduler Name";

    @AttributeDefinition(
            name = "Scheduler Name",
            type = AttributeType.STRING)
    public String cronValue() default "0 0 12 1 1 ? *";    // 0/60 * * * * ?runs midnight everyday
}
