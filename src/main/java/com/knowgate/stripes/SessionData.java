package com.knowgate.stripes;

import java.util.Date;

public interface SessionData {

	String getUser();

	void setUser(final String userId);
	
	String getPassword();

	void setPassword(final String passwords);

	String getIP();

	void setIP(final String ipAddr);
	
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

