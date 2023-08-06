package com.rij.amethyst_dev.Helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeTester {
    private long startTime;
    private long endTime;
    private String message = "";

    Logger logger = LoggerFactory.getLogger(TimeTester.class);

    public void start(String message) {
        this.message = message;
        startTime = System.nanoTime();
    }
    public void start() {
        startTime = System.nanoTime();
    }


    public void end() {
        endTime = System.nanoTime();

        logExecutionTime();
    }

    public long getExecutionTimeInNanoseconds() {
        return endTime - startTime;
    }

    public double getExecutionTimeInSeconds() {
        return getExecutionTimeInNanoseconds() / 1_000_000_000.0;
    }

    public double getExecutionTimeInMilliseconds() {
        return getExecutionTimeInNanoseconds() / 1_000_000.0;
    }

    public double getExecutionTimeInMinutes() {
        return getExecutionTimeInSeconds() / 60.0;
    }

    public void logExecutionTime() {
        double timeInSeconds = getExecutionTimeInSeconds();
        if (timeInSeconds >= 60) {
            double timeInMinutes = getExecutionTimeInMinutes();
            logger.info(message + " Execution time: " + timeInMinutes + " minutes");
        } else if (timeInSeconds >= 1) {
            logger.info(message + " Execution time: " + timeInSeconds + " seconds");
        } else {
            logger.info(message + " Execution time: " + getExecutionTimeInMilliseconds() + " ms");
        }
    }

}
