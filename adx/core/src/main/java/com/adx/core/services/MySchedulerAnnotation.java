package com.adx.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "WorkFlow Scheduler")
public @interface MySchedulerAnnotation {

    @AttributeDefinition(name = "My Scheduler", description = "SchedulerName" ,type = AttributeType.STRING)
    String schedulerName();

    @AttributeDefinition(name = "My Scheduler", description = "Scheduler CronValue", type = AttributeType.STRING)
    String cronValue();
}

