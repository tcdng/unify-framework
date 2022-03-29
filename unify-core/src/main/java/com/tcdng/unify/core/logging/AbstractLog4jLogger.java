/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Abstract logger that uses log4j library.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractLog4jLogger extends AbstractUnifyComponent implements Logger {

    private org.apache.log4j.Logger logger;

    @Override
    public void log(LoggingLevel loggingLevel, String message) throws UnifyException {
        switch (loggingLevel) {
            case DEBUG:
                logger.debug(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            case SEVERE:
                logger.fatal(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case INFO:
            default:
                logger.info(message);
                break;
        }
    }

    @Override
    public void log(LoggingLevel loggingLevel, String message, Exception exception) throws UnifyException {
        switch (loggingLevel) {
            case DEBUG:
                logger.debug(message, exception);
                break;
            case ERROR:
                logger.error(message, exception);
                break;
            case SEVERE:
                logger.fatal(message, exception);
                break;
            case WARN:
                logger.warn(message, exception);
                break;
            case INFO:
            default:
                logger.info(message, exception);
                break;
        }
    }

    @Override
    public boolean isEnabled(LoggingLevel loggingLevel) throws UnifyException {
        switch (loggingLevel) {
            case DEBUG:
                return logger.isDebugEnabled();
            case INFO:
                return logger.isInfoEnabled();
            case SEVERE:
            case WARN:
            case ERROR:
            default:
                break;
        }
        return true;
    }

    protected org.apache.log4j.Logger getLogger(String category) {
        if (logger == null) {
            logger = org.apache.log4j.Logger.getLogger(category);
        }
        return logger;
    }
}
