package com.adx.core.services;

import java.util.List;

public interface Mailer {
    void sendmail(String[] recipient, List<String> content, String pagePath);
}
