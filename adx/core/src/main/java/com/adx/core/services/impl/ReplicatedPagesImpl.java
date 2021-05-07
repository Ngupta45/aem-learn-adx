package com.adx.core.services.impl;

import com.adx.core.services.ReplicatedPages;
import com.adx.core.utils.ResolverUtil;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Component(service = ReplicatedPages.class, immediate = true)
public class ReplicatedPagesImpl implements ReplicatedPages {
    private static final Logger LOGS = LoggerFactory.getLogger(ReplicatedPagesImpl.class);

    @Activate
    public void activate(ComponentContext componentContext) {
        LOGS.info("\n ===================INSIDE ACTIVATE IN REPLICATED PAGES=========");

    }

    @Reference
    Replicator replicator;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public List<String> getNonReplicatedTitles() {
        LOGS.info("\n+++++++++++++++++++++++++++++++++++++++++++++INSIDE NON REPLICATED PAGE LIST++++++++++++++++");
        List<String> nonReplicatedPages = new ArrayList<String>();
        try {
            ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
            Session session = resourceResolver.adaptTo(Session.class);

            //send all the un replicated pages
            QueryBuilder qb = resourceResolver.adaptTo(QueryBuilder.class);
            Map pagesMap = new HashMap();
            pagesMap.put("path", "/content");
            pagesMap.put("type", "cq:Page");
            pagesMap.put("p.limit", "-1");
            Query query = qb.createQuery(PredicateGroup.create(pagesMap), session);
            query.setStart(0);
            query.setHitsPerPage(1000);
            SearchResult searchResult = query.getResult();
            for (Hit hit : searchResult.getHits()) {
                String path = null;
                try {
                    path = hit.getPath();
                    ReplicationStatus replicationStatus = replicator.getReplicationStatus(session, path);
                    Resource resource = resourceResolver.getResource(path);
                    if (replicationStatus.isActivated() == false) {
                        LOGS.info("\n resource page: - {} replication status - {} ", resource.getName(), replicationStatus.isActivated());
                        LOGS.info("/n path - {} ", resource.getPath());
                        nonReplicatedPages.add(resource.getName());
                    }
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }
            return nonReplicatedPages;
        } catch (LoginException | NullPointerException e) {
            e.printStackTrace();
            LOGS.info("\n Login Exception message: -{}  error: -{} ", e.getMessage(), e);
        }
        return nonReplicatedPages;
    }
}
