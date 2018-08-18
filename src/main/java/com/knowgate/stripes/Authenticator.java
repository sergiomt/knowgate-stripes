package com.knowgate.stripes;

import com.knowgate.encryption.AuthenticationOutcome;
import com.knowgate.encryption.PasswordEncryption;

public interface Authenticator {

	AuthenticationOutcome autenticate (final Credentials credentials);

	Credentials decrypt (final String authenticationString, final PasswordEncryption encryptionMethod) throws IllegalArgumentException;

	String encrypt (final String authenticationString, final PasswordEncryption encryptionMethod);

}
