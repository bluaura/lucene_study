package com.example.logger;

import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MdcLoggingExample {
    public static void main(String[] args) {
//        String processId = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
//        MDC.put("processId", processId);
        MDC.put("processId", "LoggingExample");

        Logger logger = LoggerFactory.getLogger(LoggingExample.class);
        logger.debug("This is a debug message");
        logger.info("This is an info message");
        logger.warn("This is a warn message");
        logger.error("This is an error message");

        try {
            throw new Exception("Test exception");
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }
}
