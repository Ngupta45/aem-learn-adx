package com.adx.core.listeners;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
//import com.myaemproject.core.services.NonReplicatedPages;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.LoginException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component(service = NonReplicatedPages.class,immediate = true)
public class NonReplicatedPagesImpli implements NonReplicatedPages {

    private static Logger log= LoggerFactory.getLogger(NonReplicatedPagesImpli.class);

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    @Reference
    protected Replicator replicator;

    @Override
    public List<String> getNonReplicatedPages() {

        log.info("-------getNonReplicatedPages() start-------");

        List<String> nonReplicatedPages = new ArrayList<>();
        try{
            HashMap<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "sakshi");
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
            Session session = resourceResolver.adaptTo(Session.class);

            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page page= (Page) pageManager.getPage("/content/adx");
            Iterator<Page> pageIterator=page.listChildren(new PageFilter(),true);

            while(pageIterator.hasNext()){
                Page childPage=pageIterator.next();
                ReplicationStatus replicationStatus=replicator.getReplicationStatus(session,childPage.getPath());
                if(replicationStatus.isActivated()==false){
                    log.info("Non repliacted pages are:",childPage.getName());
                    nonReplicatedPages.add(childPage.getName());
                }
            }
        } catch (org.apache.sling.api.resource.LoginException e) {
            e.printStackTrace();
        }
        log.info("-------getNonReplicatedPages() end-------");
        return nonReplicatedPages;
    }
}