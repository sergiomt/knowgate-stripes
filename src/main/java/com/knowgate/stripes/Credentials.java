package com.knowgate.stripes;

import com.knowgate.encryption.PasswordEncryption;

public class Credentials {

	private String userId;

	private String secret;

	private PasswordEncryption encryptionMethod;

	public Credentials(final String userId, final String password) {
		setUser(userId);
		setSecret(password);
		setEncryptionMethod(PasswordEncryption.CLEAR_TEXT);
	}

	public Credentials(final String userId, final String password, final PasswordEncryption encryptionMethod) {
		setUser(userId);
		setSecret(password);
		setEncryptionMethod(encryptionMethod);
	}

	public String getUser() {
		return userId;
	}

	public void setUser(final String userId) {
		this.userId = userId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(final String password) {
		this.secret = password;
	}

	public PasswordEncryption getEncryptionMethod() {
		return encryptionMethod;
	}

	public void setEncryptionMethod(final PasswordEncryption encryptionMethod) {
		this.encryptionMethod = encryptionMethod;
	}
}
