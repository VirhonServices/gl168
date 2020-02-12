package com.virhon.fintech.gl;

import java.time.LocalDate;

public class Config {
    private static Config INSTANCE = null;

    public static Config getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Config();
        }
        return INSTANCE;
    }

    public int getMaxNumPostsInBlock() {
        return 120;
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
}
