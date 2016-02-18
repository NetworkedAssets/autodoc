package com.networkedassets.autodoc.jsondoclet;

import org.slf4j.Logger;

/**
 * Enumeration implementing the LoggingLevel interface and sending all messages
 * to a SLF4j logger.
 * 
 * @author markus
 */
public enum LoggingLevelEnum implements LoggingLevel {
	INFO {
		@Override
		public void log(Logger log, String message) {
			log.info(message);
		}
	},
	WARN {
		@Override
		public void log(Logger log, String message) {
			log.warn(message);
		}
	},
	ERROR {
		@Override
		public void log(Logger log, String message) {
			log.error(message);
		}
	};
}