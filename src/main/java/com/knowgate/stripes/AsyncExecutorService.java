package com.knowgate.stripes;

/**
 * © Copyright 2016 the original author.
 * This file is licensed under the Apache License version 2.0.
 * You may not use this file except in compliance with the license.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.
 */

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * Extends ThreadPoolExecutor for usage by asynchronous action beans
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class AsyncExecutorService extends ThreadPoolExecutor {
	
	public static int availableProcessors = -1;
	public static int maxThreadsPerProcessor = 8;
	public static int keepAliveMins = 5;
	private static AsyncExecutorService defaultExecutor = null;

	/**
	 * Create and executor service with 1 initial thread per available processor, 8 maximum threads by available processor, 5 minutes keep alive and unbounded runnable queue
	 */
	public AsyncExecutorService() {
		super(processors(), processors()*maxThreadsPerProcessor, keepAliveMins, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	}

	private static int processors() {
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		return availableProcessors==-1 ? os.getAvailableProcessors() : availableProcessors;
	}
	
	public static AsyncExecutorService getDefaultExecutor() {
		if (null==defaultExecutor)
			defaultExecutor = new AsyncExecutorService();
		return defaultExecutor;
	}
}
