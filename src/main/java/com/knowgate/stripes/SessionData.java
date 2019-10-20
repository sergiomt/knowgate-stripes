package com.knowgate.stripes;

import java.util.Date;

public interface SessionData {

	String getSessionId();

	void setSessionId(final String sessionId);

	String getUser();

	void setUser(final String userId);

	String getSecret();

	void setSecret(final String secret);

	Fingerprint getFingerprint();

	void setFingerprint(final Fingerprint fingerprint);

	String[] getIPs();

	void addIP(final String ipAddr);

	Date getStart();

	void setStart(final Date dateStart);

	void setStart(final long dateStartMillis);

	Date getLastAccess();

	long getLastAccessL();

	void setLastAccess(final Date dateLastAccess);

	void setLastAccess(final long dateLastAccessMillis);

	Date getEnd();

	void setEnd(final Date dateEnd);

	void setEnd(final long dateEndMillis);

}

