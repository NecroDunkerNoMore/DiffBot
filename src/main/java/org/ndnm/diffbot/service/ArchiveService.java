package org.ndnm.diffbot.service;

import org.ndnm.diffbot.model.ArchivedUrl;
import org.ndnm.diffbot.model.diff.DiffUrl;

public interface ArchiveService extends HealthCheckableService {
    ArchivedUrl archive(DiffUrl diffUrl);
}
