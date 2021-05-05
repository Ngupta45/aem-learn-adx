package com.adx.core.service.impl;


import com.adx.core.service.EmailProcess;
import com.adx.core.service.RepPage;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component(service = EmailProcess.class,immediate = true)
public class RepPageImpl implements RepPage {
    private static Logger log= LoggerFactory.getLogger(EmailProcess.class);

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;
    @Reference
    protected Replicator replicator;

    @Override
    public List<String> getNonReplicatedPages() {
        List<String> nonReplicatedPages = new ArrayList<>();
        try{
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(ResourceResolverFactory.SUBSERVICE, "sysUser");
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(hashMap);
            Session session = resourceResolver.adaptTo(Session.class);

            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page page = (Page) pageManager.getPage("/content/adx");

            Iterator<Page> pageIterator = page.listChildren(new PageFilter(),true);

            while(pageIterator.hasNext()){
                Page currentPage=pageIterator.next();
                ReplicationStatus replicationStatus=replicator.getReplicationStatus(session,currentPage.getPath());
                if(replicationStatus.isActivated()==false){
                    log.info("{}",currentPage.getPageTitle());
                    nonReplicatedPages.add(currentPage.getPageTitle());
                }
            }
        } catch (org.apache.sling.api.resource.LoginException e) {
            e.printStackTrace();
        }
        return nonReplicatedPages;
    }
}

