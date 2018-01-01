package org.ndnm.diffbot.service;

import org.ndnm.diffbot.model.diff.DiffResult;

public interface ArchiveService extends HealthCheckableService {
    void archive(DiffResult diffResult);
}
