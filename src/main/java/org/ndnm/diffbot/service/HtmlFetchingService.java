package org.ndnm.diffbot.service;

import org.ndnm.diffbot.model.diff.DiffUrl;

public interface HtmlFetchingService extends HealthCheckableService {
    String fetchHtml(DiffUrl diffUrl);
}
