package com.adx.core.services;

public interface NonReplicatedPagesMailerService {
    boolean isActive();
    String getCron();
    String getPayload();
    String[] getRecipientsMailID();
}
