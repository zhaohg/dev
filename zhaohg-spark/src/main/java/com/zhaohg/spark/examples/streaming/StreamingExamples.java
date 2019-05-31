package com.zhaohg.spark.examples.streaming;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Created by zhaohg on 2017/3/3.
 */
public class StreamingExamples {

    public static void setStreamingLogLevels() {

        Logger logger = Logger.getLogger(StreamingExamples.class);
        if (logger != null) {
            logger.info("Setting log level to [WARN] for streaming example." +
                    " To override add a custom log4j.properties to the classpath.");
            logger.setLevel(Level.WARN);
        }
    }
}
