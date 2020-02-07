package com.virhon.fintech.gl;

public class Config {
    private Integer maxNumPostsInBlock;

    private static Config INSTANCE = null;

    public static Config getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Config();
        }
        return INSTANCE;
    }

    public int getMaxNumPostsInBlock() {
        return 100;
    }

    /**
     * Returns maximum limit of reserving duration in seconds
     *
     * @return      - maximum limit of reserving duration in seconds
     */
    public int getReservigDuration() {
        return 300;
    }
}
