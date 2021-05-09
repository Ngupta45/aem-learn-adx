package com.adx.core.services;

import java.util.List;

public interface NonReplicatedPageFinder {

    List<String> getNonReplicatedPages(String pagePath);
}