package com.knowgate.stripes;

final class SessionReaper extends Thread {

	/**
	 * Reference to reaped LoginInterceptor
	 */
	private LoginInterceptor logint;

	/**
	 * Used to stop the Connection reaper thread
	 */
	private boolean keepruning;

	/**
	 * Session Reaper call interval (default = 10 mins)
	 */
	private long delay = 600000l;

	/**
	 * <p>Constructor</p>
	 * @param forlogint LoginInterceptor
	 */
	SessionReaper(LoginInterceptor forlogint) {
		logint = forlogint;
		keepruning = true;
		try {
			checkAccess();
			setDaemon(true);
			setPriority(MIN_PRIORITY);
		} catch (SecurityException ignore) { }
	}

	/**
	 * Get session reaper call interval
	 * @return long Number of milliseconds between reaper calls
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * <p>Set session reaper call interval</p>
	 * The default value is 10 minutes
	 * @param lDelay long Number of milliseconds between reaper calls
	 * @throws IllegalArgumentException if lDelay is less than 1000
	 */
	public void setDelay(long lDelay) throws IllegalArgumentException {
		if (lDelay<1000l && lDelay>0l)
			throw new IllegalArgumentException("SessionReaper delay cannot be smaller than 1000 miliseconds");
		delay=lDelay;
	}

	public void halt() {
		keepruning=false;
	}

	/**
	 * Reap sessions every n-minutes
	 */
	public void run() {
		while (keepruning) {
			try {
				sleep(delay);
			} catch( InterruptedException e) { }
			if (keepruning) logint.reapSessions(delay);
		} // wend
	} // run
} // SessionReaper
