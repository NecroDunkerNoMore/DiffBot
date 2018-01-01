package org.ndnm.diffbot.service.impl;

import java.io.Closeable;
import java.io.IOException;

import org.apache.logging.log4j.Logger;

public abstract class HttpConnectionCloser {

    protected abstract Logger getLog();

    protected void closeHttpObjects(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    getLog().warn("Could not close response/client!: " + e.getMessage());
                }
            }
        }
    }

}
