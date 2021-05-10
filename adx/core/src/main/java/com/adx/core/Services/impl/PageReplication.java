package com.mysite.core.services.impl;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.mysite.core.services.ReplicatedPages;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.*;
@Component(
        service = ReplicatedPages.class,
        immediate = true,
        property = {
                "label=My Service Implementation"
        }
)

public class PageReplication implements ReplicatedPages
{
    private static final Logger log = LoggerFactory.getLogger(PageReplication.class);
    private ResourceResolver resourceResolver;
    @Reference
    Replicator replicator;
    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public List<String> NonReplicated()
    {
        List<String>nonreplicatedpages=new ArrayList<>();
       try
       {
           Map<String, Object> param = new HashMap<>();
           param.put(ResourceResolverFactory.SUBSERVICE, "user-test1");
           resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
           PageManager pageManager=resourceResolver.adaptTo(PageManager.class);
           Session session= resourceResolver.adaptTo(Session.class);
           Page page= pageManager.getPage("/content/mysite");
           Iterator<Page> pageIterator=page.listChildren(null,true);
           while(pageIterator.hasNext())
           {
               Page children=pageIterator.next();
               ReplicationStatus replicationStatus=replicator.getReplicationStatus( session,children.getPath());
               if(replicationStatus.isActivated()==false)
               {
                   nonreplicatedpages.add(children.getTitle());
               }
           }



       } catch (LoginException e)
       {
           e.printStackTrace();
       }


        return nonreplicatedpages;
    }
}
