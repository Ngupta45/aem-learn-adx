package com.adx.core.schedulers;
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
@Component(service = Nonreplicatingpages.class,immediate = true)
public class Nonreplicatingimpl implements Nonreplicatingpages {
    private static Logger log= LoggerFactory.getLogger(Nonreplicatingimpl.class);
    @Reference
    protected ResourceResolverFactory resourceResolverFactory;
    @Reference
    protected Replicator replicator;
    @Override
    public List<String> getNonReplicatedPages() {
        List<String> nonReplicatedPages = new ArrayList<>();
        try{
            HashMap<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "shivanshsystemuser");
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
            Session session = resourceResolver.adaptTo(Session.class);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page page= (Page) pageManager.getPage("/content/adx");
            Iterator<Page> pageIterator=page.listChildren(new PageFilter(),true);
            while(pageIterator.hasNext()){
                Page descendentPage=pageIterator.next();
                ReplicationStatus replicationStatus=replicator.getReplicationStatus(session,descendentPage.getPath());
                if(replicationStatus.isActivated()==false){
                    log.info("Non repliacted pages are:",descendentPage.getPageTitle());
                    nonReplicatedPages.add(descendentPage.getName());
                }
            }
        } catch (org.apache.sling.api.resource.LoginException e) {
            e.printStackTrace();
        }

        return nonReplicatedPages;
    }
}