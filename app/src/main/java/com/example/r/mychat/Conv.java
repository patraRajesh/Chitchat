package com.example.r.mychat;

/**
 * Created by r on 1/16/2018.
 */

public class Conv {


    private boolean seen;

    private long timestamp;
    public Conv(){}

    public Conv(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
