package com.virhon.fintech.gl;

import org.springframework.beans.factory.annotation.Value;

public class Config {
    @Value("${MAX_NUM_POSTS_IN_BLOCK}")
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
