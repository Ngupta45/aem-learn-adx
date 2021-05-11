package com.adx.core.services;
import org.apache.sling.api.resource.LoginException;

import java.util.List;

public interface NonReplicatedPages {
    List<String> getAllNonReplicatedPages() throws LoginException;
}
