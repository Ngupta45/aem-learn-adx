package com.adx.core.services;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component(service = ReplicatedPagesService.class, immediate = true)
public class ReplicatedPagesService {

    ResourceResolverFactory resourceResolverFactory;
    Replicator replicator;
    ResourceResolver resourceResolver;

    public List<String>getNonReplicatedPages() throws LoginException
    {
        List<String> nrpList = new ArrayList<>();

        HashMap<String,Object>hm = new HashMap<>();
        hm.put(ResourceResolverFactory.SUBSERVICE, "mySystemUser");
        resourceResolver = resourceResolverFactory.getServiceResourceResolver(hm);
        Session session = resourceResolver.adaptTo(Session.class);
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page page = (Page) pageManager.getPage("/content/usergroupassign");
        Iterator<Page> pageIterator = page.listChildren(new PageFilter(), true);
        while(pageIterator.hasNext())
        {
            Page nextPage = pageIterator.next();
            ReplicationStatus replicationStatus = replicator.getReplicationStatus(session,nextPage.getPath());

            if(replicationStatus.isActivated() == false)
            {
                nrpList.add(nextPage.getPageTitle());
            }
        }
        return nrpList;

    }


}
