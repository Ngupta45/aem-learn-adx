package com.adx.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Non Replicated Pages Mailer Workflow Scheduler"
)
public @interface NonReplicatedPagesMailerOCD {
    @AttributeDefinition(
            name = "Activate",
            description = "Check to activate scheduler",
            type = AttributeType.BOOLEAN)
    boolean isActive();

    @AttributeDefinition(
            name = "Schedule Time",
            description = "Enter cron expression",
            type = AttributeType.STRING)
    String getCron();

    @AttributeDefinition(
            name = "Enter Payload",
            description = "Enter path to find non replicated pages",
            type = AttributeType.STRING)
    String getPayload();

    @AttributeDefinition(
            name = "Recipients Email ID",
            description = "Enter recipients email ID",
            type = AttributeType.STRING)
    String[] getRecipientsMailID();
}
