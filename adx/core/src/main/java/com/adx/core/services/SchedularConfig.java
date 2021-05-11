package com.adx.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Custom Workflow Schedular with email feature",
        description = "this is schedular config"
)
public @interface SchedularConfig {
    @AttributeDefinition(
            name = "Schedular name",
            type = AttributeType.STRING
    )
    public String getSchedularName() default "Dummy Schedular";

    @AttributeDefinition(
            name = "Repition time",
            type = AttributeType.STRING
    )
    public String getCronTime() default "0 0 0 1/1 * ? *";


}

