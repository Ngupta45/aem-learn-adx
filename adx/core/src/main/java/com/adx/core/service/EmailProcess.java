package com.adx.core.service;


import com.adx.core.service.impl.RepPageImpl;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {"process.label" + "=Email Process"}
        )
public class EmailProcess implements WorkflowProcess {

    Logger logger = LoggerFactory.getLogger(EmailProcess.class);

    @Reference
    RepPageImpl repPage;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap){

    }
}
