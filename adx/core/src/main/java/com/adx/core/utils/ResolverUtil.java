package com.adx.core.utils;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.HashMap;
import java.util.Map;

public final class ResolverUtil {

    public static final String GEEKS_SERVICE_USER = "aemgeekstestuser";

    public static ResourceResolver newResolver(ResourceResolverFactory resourceResolverFactory) throws LoginException {
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(ResourceResolverFactory.SUBSERVICE, GEEKS_SERVICE_USER);
        ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(paramMap);
        return resolver;
    }
}
