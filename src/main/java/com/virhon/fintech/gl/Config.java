package com.virhon.fintech.gl;

import java.time.LocalDate;

public class Config {
    private static Config INSTANCE = null;
    private int maxNumPostsInBlock = 100;

    public Config() {
    }

    public static Config getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Config();
        }
        return INSTANCE;
    }

    public LocalDate getReportedOn() {return LocalDate.now();}

    /**
     * Returns maximum limit of reserving duration in seconds
     *
     * @return      - maximum limit of reserving duration in seconds
     */
    public int getReservigDuration() {
        return 300;
    }

    public int getMaxNumPostsInBlock() {
        return maxNumPostsInBlock;
    }

    /**
     * Temporary measure to be able to test
     *
     * @param maxNumPostsInBlock
     */
    public void setMaxNumPostsInBlock(int maxNumPostsInBlock) {
        this.maxNumPostsInBlock = maxNumPostsInBlock;
    }
}
