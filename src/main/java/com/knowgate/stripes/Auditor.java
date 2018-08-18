package com.knowgate.stripes;

import com.knowgate.encryption.AuthenticationOutcome;

import net.sourceforge.stripes.action.ActionBeanContext;

public interface Auditor {

	void audit (Credentials credentials, AuthenticationOutcome outcome, ActionBeanContext actionContext);

}
