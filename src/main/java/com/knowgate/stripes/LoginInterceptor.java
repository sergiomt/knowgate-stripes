package com.knowgate.stripes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.knowgate.tuples.Pair;
import com.knowgate.encryption.AuthenticationOutcome;
import com.knowgate.encryption.PasswordEncryption;

import com.knowgate.stripes.BaseStripesBean;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;

public abstract class LoginInterceptor implements Interceptor {

	public final String ERROR_CODE_EXPIRED = "error.expiredsession";
	
	public final String ERROR_CODE_INVALID = "error.invalidsession";

	public final String ERROR_CODE_AUTHENTICATION_FAILED =  "error.authenticationfailed";

	protected Authenticator authenticator;
	
	protected Auditor auditor;

	protected final PasswordEncryption encryptionMethod;

	protected final String tokenName;

	protected static final ConcurrentHashMap<String,SessionData> sessions = new ConcurrentHashMap<String,SessionData>();

	protected static final ConcurrentSkipListSet<String> loggedIn = new ConcurrentSkipListSet<String>();

	protected List<Class<? extends BaseStripesBean>> autolog;
	
	protected List<Class<? extends BaseStripesBean>> allow;

	// ---------------------------------------------------------

	@SuppressWarnings("unused")
	private LoginInterceptor() {
		this.allow = new LinkedList<Class<? extends BaseStripesBean>>();
		this.autolog = new LinkedList<Class<? extends BaseStripesBean>>();
		this.tokenName = "SessionToken";
		this.encryptionMethod = PasswordEncryption.CLEAR_TEXT;
		this.authenticator = null;
		this.auditor = null;
	}

	// ---------------------------------------------------------

	public LoginInterceptor(String tokenName, PasswordEncryption encryptionMethod) {
		this.allow = new LinkedList<Class<? extends BaseStripesBean>>();
		this.autolog = new LinkedList<Class<? extends BaseStripesBean>>();
		this.tokenName = tokenName;
		this.encryptionMethod = encryptionMethod;
		this.authenticator = null;
		this.auditor = null;
	}

	// ---------------------------------------------------------
	
	public Auditor getAuditor() {
		return auditor;
	}
	
	
	// ---------------------------------------------------------
	
	public void setAuditor(Auditor auditorImpl) {
		auditor = auditorImpl;
	}

	
	// ---------------------------------------------------------
	
	public Authenticator getAuthenticator() {
		return authenticator;
	}
	
	
	// ---------------------------------------------------------
	
	public void setAuthenticator(Authenticator authenticatorImpl) {
		authenticator = authenticatorImpl;
	}

	// ---------------------------------------------------------

	public boolean isLoggedIn(final String userId) {
		return userId==null ? false : loggedIn.contains(userId);
	}

	// ---------------------------------------------------------

	protected boolean isAjaxBean(Class<? extends BaseStripesBean> clazz) {
		if (clazz==null)
			return false;
		else
			return BaseAjaxBean.class.isAssignableFrom(clazz);
	}

	// ---------------------------------------------------------

	public List<Class<? extends BaseStripesBean>> getAllowed() {
		return allow;
	}

	// ---------------------------------------------------------

	public boolean isAllowed(Class<? extends BaseStripesBean> clazz) {
		return getAllowed().contains(clazz);
	}

	// ---------------------------------------------------------

	public void setAllowed(List<Class<? extends BaseStripesBean>> allowed) {
		allow = allowed;
	}

	// ---------------------------------------------------------

	public List<Class<? extends BaseStripesBean>> getAutologged() {
		return autolog;
	}

	// ---------------------------------------------------------

	public boolean isAutologged(Class<? extends BaseStripesBean> clazz) {
		return getAutologged().contains(clazz);
	}

	// ---------------------------------------------------------

	public void setAutolog(List<Class<? extends BaseStripesBean>> autologged) {
		autolog = autologged;
	}

	// ---------------------------------------------------------

	public abstract SessionData getSessionData(final String sessionToken);

	// ---------------------------------------------------------

	public abstract Resolution resolve(Class<? extends BaseStripesBean> clazz, final String errorCode, final String lastUrl);

	// ---------------------------------------------------------

	public abstract Resolution handleException(Class<? extends BaseStripesBean> beanClass, Exception xcpt);
	
	// ---------------------------------------------------------

	protected abstract Pair<Boolean,String> autolog(Map<String,Object> params);
	
	// ---------------------------------------------------------

	public abstract String createSession(Credentials credentials, final String sIPAddr) throws IllegalStateException, InstantiationException;
	
	// ---------------------------------------------------------

	public abstract void closeSession(final String sessionToken);

	// ---------------------------------------------------------
	
	/**
	 * Called from the session reaper daemon thread every n-minutes for maintaining the pool clean
	 */
	public abstract void reapSessions(final long lMaxKeepAlive);
	

	// ---------------------------------------------------------

	public Resolution intercept(ExecutionContext execContext) {
		Resolution resolution = null;
		Class<? extends BaseStripesBean> beanClass = null;
		try {
			resolution = execContext.proceed();
			if (null!=resolution) {
				BaseStripesBean baseBean = (BaseStripesBean) execContext.getActionBean();
				if (null==baseBean) return new ErrorResolution(404,"Action Bean not found");
				ActionBeanContext oCtx = baseBean.getContext();
				beanClass = baseBean.getClass();

					if (isAutologged(beanClass)) {
						Map<String,Object> autologParams = new HashMap<String,Object>();
						autologParams.put("ip", oCtx.getRequest().getRemoteAddr());
						autologParams.put("code", oCtx.getRequest().getRemoteAddr());
						Pair<Boolean,String> autologResult = autolog(autologParams);
					  if (autologResult.$1())
					  	baseBean.setParam(tokenName, autologResult.$2());
					  else
						resolution = resolve(beanClass, autologResult.$2(), baseBean.getLastUrl());
					} else if (!isAllowed(beanClass)) {
						String sTkn = baseBean.getSessionToken();
						if (null==sTkn) {
							resolution = resolve(beanClass, ERROR_CODE_EXPIRED, baseBean.getLastUrl());
						} else if (sTkn.length()==0) {
							resolution = resolve(beanClass, ERROR_CODE_EXPIRED, baseBean.getLastUrl());
						} else if (!sessions.containsKey(sTkn)) {
							Credentials credentials = authenticator.decrypt(sTkn, encryptionMethod);
							if (null==credentials) {
								resolution = resolve(beanClass, ERROR_CODE_INVALID, baseBean.getLastUrl());
							} else {
								AuthenticationOutcome authOutcome = authenticator.autenticate(credentials);
								if (auditor!=null)
									auditor.audit(credentials, authOutcome, baseBean.getContext());
								if (authOutcome!=AuthenticationOutcome.OK && authOutcome!=AuthenticationOutcome.ACCOUNT_UNCONFIRMED) {
									resolution = resolve(beanClass, ERROR_CODE_AUTHENTICATION_FAILED, baseBean.getLastUrl());
								}
							}
						}
					}
			}
		} catch (Exception xcpt) {
			resolution = handleException(beanClass, xcpt);
		}
		return resolution;
	}

}