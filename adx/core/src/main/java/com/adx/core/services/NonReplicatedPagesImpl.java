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
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component(service = NonReplicatedPages.class, immediate = true)
class NonReplicatedpagesImpl implements NonReplicatedPages {

    @Reference
    Replicator replicator;

    @Reference
    ResourceResolverFactory resolverFactory;

    private static final Logger log = LoggerFactory.getLogger(NonReplicatedpagesImpl.class);

    @Override
    public List<String> getAllNonReplicatedPages() throws LoginException {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put(ResourceResolverFactory.SUBSERVICE, "testServiceUser");
        ResourceResolver resourceResolver = resolverFactory.getResourceResolver(paramMap);
        List<String> nonReplicatedPages = new ArrayList<>();
        Session session = resourceResolver.adaptTo(Session.class);
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page page = pageManager.getPage("/content/slingproject/us");
        Iterator<Page> pageIterator = page.listChildren(new PageFilter(), true);
        log.info("{}", "--------------------Non Replicated pages logs-----------------");
        while (pageIterator.hasNext()) {
            Page descendentPage = pageIterator.next();
            ReplicationStatus replicationStatus = replicator.getReplicationStatus(session, descendentPage.getPath());
            if (replicationStatus.isActivated() == false) {
                log.info("{}", descendentPage.getTitle());
                nonReplicatedPages.add(descendentPage.getPageTitle());
            }
        }
        return nonReplicatedPages;
    }
}

