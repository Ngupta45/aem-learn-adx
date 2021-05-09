package com.adx.core.services.impl;

import com.adx.core.services.NonReplicatedPagesMailerOCD;
import com.adx.core.services.NonReplicatedPagesMailerService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = NonReplicatedPagesMailerService.class, immediate = true)
@Designate(ocd = NonReplicatedPagesMailerOCD.class)
public class NonReplicatedPagesMailerServiceImpl implements NonReplicatedPagesMailerService {

    private boolean active;
    private String cron;
    private String payload;
    private String[] recipients;

    @Activate
    void init(NonReplicatedPagesMailerOCD ocdObj) {
        active = ocdObj.isActive();
        cron = ocdObj.getCron();
        payload = ocdObj.getPayload();
        recipients = ocdObj.getRecipientsMailID();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getCron() {
        return cron;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    @Override
    public String[] getRecipientsMailID() {
        return recipients;
    }
}
