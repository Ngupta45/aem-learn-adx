package com.adx.core.services.impl;

import com.adx.core.services.NonReplicatedPageFinder;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.*;

@Component(service = NonReplicatedPageFinder.class, immediate = true)
public class NonReplicatedPageFinderImpl implements NonReplicatedPageFinder {

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    @Reference
    protected Replicator replicator;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String SERVICE_NAME = "content-page-user";

    private static final Map<String, Object> USER_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);


    @Override
    public List<String> getNonReplicatedPages(String pagePath) {
        List<String> nonReplicatedPages = new ArrayList<>();
        ResourceResolver resourceResolver = null;
        Session session = null;
        PageManager pageManager;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(USER_INFO);
            session = resourceResolver.adaptTo(Session.class);
            pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                Page rootPage = pageManager.getPage(pagePath);
                Iterator<Page> pageIterator = rootPage.listChildren(null, true);
                while (pageIterator.hasNext()) {
                    Page childPage = pageIterator.next();
                    ReplicationStatus replicationStatus = replicator.getReplicationStatus(session, childPage.getPath());
                    if (!replicationStatus.isActivated() || replicationStatus.isDeactivated()) {
                        nonReplicatedPages.add(childPage.getName());
                    }
                }
            }
        } catch (LoginException e) {
            LOG.info("Exception Occurred: {}", e.getMessage(), e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
            if (session != null) {
                session.logout();
            }
        }
        return nonReplicatedPages;
    }
}
