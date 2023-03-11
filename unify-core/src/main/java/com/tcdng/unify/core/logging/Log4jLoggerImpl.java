/*
 * Copyright 2018-2023 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.unify.core.logging;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;

/**
 * Default implementation of a log4j logger.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class Log4jLoggerImpl extends AbstractLog4jLogger {

    @Configurable
    private String logCategory;

    @Configurable("%d{yyyy-MM-dd HH:mm:ss} %-5p %m%n")
    private String logPattern;

    @Configurable("true")
    private boolean logToConsole;

    @Configurable("true")
    private boolean logToFile;

    @Configurable
    private String logFilename;

    @Configurable("4MB")
    private String logFileMaxSize;

    @Configurable("debug")
    private String logLevel;

    @Configurable("8")
    private int maxFileBackup;

    public void setLogCategory(String logCategory) {
        this.logCategory = logCategory;
    }

    public void setLogPattern(String logPattern) {
        this.logPattern = logPattern;
    }

    public void setLogToConsole(boolean logToConsole) {
        this.logToConsole = logToConsole;
    }

    public void setLogToFile(boolean logToFile) {
        this.logToFile = logToFile;
    }

    public void setLogFilename(String logFilename) {
        this.logFilename = logFilename;
    }

    public void setLogFileMaxSize(String logFileMaxSize) {
        this.logFileMaxSize = logFileMaxSize;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public void setMaxFileBackup(int maxFileBackup) {
        this.maxFileBackup = maxFileBackup;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onInitialize() throws UnifyException {
        try {
            org.apache.log4j.Logger logger = getLogger(logCategory);
            synchronized (logger) {
                // Ensure that appenders for a particular logger category are
                // not added
                // multiple times
                boolean appendConsole = true;
                boolean appendFile = true;
                Enumeration<Appender> appenders = (Enumeration<Appender>) logger.getAllAppenders();
                while (appenders.hasMoreElements() && (appendConsole || appendFile)) {
                    Appender appender = appenders.nextElement();
                    if (appender instanceof ConsoleAppender) {
                        appendConsole = false;
                    } else if (appender instanceof RollingFileAppender) {
                        RollingFileAppender rfAppender = (RollingFileAppender) appender;
                        if (logFilename.equalsIgnoreCase(rfAppender.getFile())) {
                            appendFile = false;
                        }
                    }
                }

                PatternLayout patternLayout = new PatternLayout(logPattern);
                if (appendConsole && logToConsole) {
                    logger.addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT));
                }

                if (appendFile && logToFile) {
                    RollingFileAppender rfAppender = new RollingFileAppender(patternLayout, logFilename);
                    rfAppender.setMaxFileSize(logFileMaxSize);
                    rfAppender.setMaxBackupIndex(maxFileBackup);
                    logger.addAppender(rfAppender);
                }
            }
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INITIALIZATION_ERROR, getName());
        }
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
