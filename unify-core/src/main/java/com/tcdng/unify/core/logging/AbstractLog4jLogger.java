/*
 * Copyright 2018-2025 The Code Department.
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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.Unify;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Convenient abstract base class for log4j loggers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractLog4jLogger extends AbstractUnifyComponent implements Logger {

	private static final int FILE_MAX_BACKUP = 4;

	private LoggerContext loggerCtx;

	private org.apache.logging.log4j.Logger logger;

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

	protected org.apache.logging.log4j.Logger getLogger(String category) throws UnifyException {
		if (logger == null) {
			synchronized (this) {
				if (logger == null) {
					logger = getLoggerContext().getLogger(category);
				}
			}
		}

		return logger;
	}

	private LoggerContext getLoggerContext() throws UnifyException {
		if (loggerCtx == null) {
			synchronized (AbstractLog4jLogger.class) {
				if (loggerCtx == null) {
					try {
						ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory
								.newConfigurationBuilder();
						String loggingPattern = Unify.getSetting(String.class,
								UnifyCorePropertyConstants.APPLICATION_LOGGER_PATTERN_SETTING);
						if (loggingPattern == null) {
							loggingPattern = "%d{ISO8601} %-5p -- [%-36.36c{1}] : %m%n";
						}

						boolean logToConsole = Unify.getSetting(boolean.class,
								UnifyCorePropertyConstants.APPLICATION_LOG_TO_CONSOLE);
						if (logToConsole) {
							AppenderComponentBuilder consoleAB = builder.newAppender("stdout", "Console")
									.add(builder.newLayout("PatternLayout")
											.addAttribute("pattern", loggingPattern))
									.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
							builder.add(consoleAB);
						}

						boolean logToFile = Unify.getSetting(boolean.class,
								UnifyCorePropertyConstants.APPLICATION_LOG_TO_FILE);
						if (logToFile) {
							String filename = Unify.getSetting(String.class,
									UnifyCorePropertyConstants.APPLICATION_LOG_FILENAME);
							if (filename == null) {
								filename = "application.log";
							}
							filename = IOUtils.buildFilename("logs", filename);
							filename = IOUtils.buildFilename(Unify.getWorkingPath(), filename);

							String fileMaxSize = Unify.getSetting(String.class,
									UnifyCorePropertyConstants.APPLICATION_LOG_FILEMAXSIZE);
							if (fileMaxSize == null) {
								fileMaxSize = "5MB";
							}

							int fileMaxBackup = Unify.getSetting(int.class,
									UnifyCorePropertyConstants.APPLICATION_LOG_FILEMAXBACKUP);
							if (fileMaxBackup <= 0) {
								fileMaxBackup = FILE_MAX_BACKUP;
							}

							AppenderComponentBuilder rollingAB = builder.newAppender("rolling", "RollingFile")
									.addAttribute("fileName", filename)
									.addAttribute("filePattern", filename + ".%i")
									.add(builder.newLayout("PatternLayout")
											.addAttribute("pattern", loggingPattern))
									.addComponent(builder.newComponent("Policies")
											.addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
													.addAttribute("size", fileMaxSize)))
									.addComponent(builder.newComponent("DefaultRolloverStrategy").addAttribute("max",
											String.valueOf(fileMaxBackup)));
							builder.add(rollingAB);
						}

						final String level = Unify.getSetting(String.class,
								UnifyCorePropertyConstants.APPLICATION_LOG_LEVEL);
						final Level rootLevel = StringUtils.isNotBlank(level) ? Level.toLevel(level.toUpperCase())
								: Level.OFF;
						RootLoggerComponentBuilder rootLB = builder.newRootLogger(rootLevel);
						if (logToConsole) {
							rootLB.add(builder.newAppenderRef("stdout"));
						}

						if (logToFile) {
							rootLB.add(builder.newAppenderRef("rolling"));
						}

						builder.add(rootLB);
						loggerCtx = Configurator.initialize(builder.build());
					} catch (Exception e) {
						throwOperationErrorException(e);
					}
				}
			}
		}

		return loggerCtx;
	}
}
