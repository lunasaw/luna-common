package com.luna.common.net.download;

import java.util.EventObject;

public class DownloadThreadEvent extends EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Object sourObject;
    long count;


    public DownloadThreadEvent(Object sourceObject, long count) {
        super(sourceObject);
        this.sourObject = sourceObject;
        this.count = count;
    }

    long getCount() {
        return count;
    }

    Object getTarget() {
        return sourObject;
    }
}
