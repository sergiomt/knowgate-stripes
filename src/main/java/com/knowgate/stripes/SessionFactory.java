package com.knowgate.stripes;

public interface SessionFactory {

	String createSession(final Credentials credentials, final String ipAddress) throws IllegalStateException, InstantiationException;

}
