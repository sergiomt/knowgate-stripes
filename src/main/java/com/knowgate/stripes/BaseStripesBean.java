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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.AsyncResponse;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.validation.SimpleError;

/**
 * <p>Base class for beans serving a response to be used as HTML page data</p>
 * @author Sergio Montoro Ten
 * @version 1.0
 * @see <a href="https://stripesframework.atlassian.net/wiki/display/STRIPES/Home">Stripes</a>
 */
public abstract class BaseStripesBean implements ActionBean {

	public static final String DEFAULT_RESOURCE_BASE = "/WEB-INF/classes/";
	public static final String DEFAULT_BUNDLE = "StripesResources";

	private ActionBeanContext context;
	private String resourceBase;
	private String bundleName;
	private ResourceBundle bundle;

	/**
	 * Create action bean with resource base path /WEB-INF/classes/ and resource bundle prefix StripesResources
	 */
	public BaseStripesBean() {
		this(DEFAULT_RESOURCE_BASE, DEFAULT_BUNDLE);
	}

	/**
	 * Create action bean
	 * @param resourceBasepath String resource base path
	 * @param resourceBundleName String resource bundle prefix
	 */
	public BaseStripesBean(String resourceBasepath, String resourceBundleName) {
		resourceBase = resourceBasepath;
		bundleName = resourceBundleName;
		bundle = null;
	}

	/**
	 * @return ActionBeanContext
	 */
	@Override
	public ActionBeanContext getContext() {
		return context;
	}

	/**
	 * @param actionBeanContext
	 */
	@Override
	public void setContext(ActionBeanContext actionBeanContext) {
		context = actionBeanContext;
	}

	/**
	 * <p>Call HttpServletRequest.getServletPath() and return the substring after the last slash / and before the first hash # after the slash (if present).</p>
	 * @return String
	 */
	public String getPageName() {
		String sPageName = getContext().getRequest().getServletPath();
		int iSlash = sPageName.lastIndexOf('/');
		if (iSlash >= 0) {
			sPageName = sPageName.substring(iSlash + 1);
			int iDot = sPageName.lastIndexOf('.');
			if (iDot > 0)
				sPageName = sPageName.substring(0, iDot);
		}
		int iHash = sPageName.indexOf('#');
		if (iHash>0)
			sPageName = sPageName.substring(++iHash);
		return sPageName;
	}

	/**
	 * <p>Get string resource from the associated ResourceBundle and replace {n} parameter with given values.</p>
	 * <p>Parameters must be numbered starting with 1. So at the resource bundle the resource will look like:</p>
	 * key=Phrase with parameter {1} and {2}
	 * @param key String Resource name
	 * @param params String[] Parameter values
	 * @return String
	 */
	public String getResource(String key, String... params) {
		if (null == bundle)
			bundle = ResourceBundle.getBundle(bundleName, getContext().getRequest().getLocale());
		int p = 0;
		String sRetVal = bundle.getString(key);
		if (sRetVal != null && params != null)
			for (String s : params)
				sRetVal = sRetVal.replace("{" + String.valueOf(++p) + "}", s);
		return sRetVal;
	}

	/**
	 * <p>Put content of an InputStream into a StringBuffer. Character encoding is assumed to be UTF-8.</p>
	 * @param instrm InputStream
	 * @return StringBuffer
	 * @throws IOException
	 */
	private StringBuffer pipeStreamToStringBuffer(InputStream instrm) throws IOException {
		StringBuffer oStr = new StringBuffer();
		char[] Buffer = new char[4000];

		InputStreamReader oReader;
		try {
			oReader = new InputStreamReader(instrm, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			oReader = null;
		}

		while (true) {
			int iReaded = oReader.read(Buffer, 0, 4000);

			if (-1 == iReaded)
				break;

			// Skip FF FE character mark for Unidode files
			int iSkip = ((int) Buffer[0] == 65279 || (int) Buffer[0] == 65534 ? 1 : 0);

			oStr.append(Buffer, iSkip, iReaded - iSkip);
		} // wend

		oReader.close();
		instrm.close();

		return oStr;
	}

	/**
	 * <p>Call ServletContect.getResourceAsStream() and convert the returned InputStream into a StringBuffer.</p>
	 * @param fileName String Name of resource file to be read.
	 * @return StringBuffer
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public StringBuffer getResourceAsStringBuffer(String fileName) throws FileNotFoundException, IOException {
		InputStream oIoStrm = getContext().getServletContext().getResourceAsStream(resourceBase + fileName);

		if (null == oIoStrm)
			throw new FileNotFoundException("File not found " + resourceBase + fileName);

		return pipeStreamToStringBuffer(oIoStrm);
	}

	/**
	 * <p>Get localized resource file as StringBuffer.</p>
	 * @param fileName String Prefix of of resource file to be read.
	 * @param language String Language code. It is expected to be appended after the file name with an underscore like "fileName_es" or "fileName_fr"
	 * @return StringBuffer
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public StringBuffer getResourceAsStringBuffer(String fileName, String language)
			throws FileNotFoundException, IOException {
		String localizedFileName = fileName;
		int iDot = localizedFileName.lastIndexOf('.');
		if (iDot >= 0) {
			String sLocalizedFileNameWithoutExt = localizedFileName.substring(0, iDot);
			if (!sLocalizedFileNameWithoutExt.endsWith("_" + language))
				sLocalizedFileNameWithoutExt += "_" + language;
			localizedFileName = sLocalizedFileNameWithoutExt + localizedFileName.substring(iDot);
		} else {
			if (!localizedFileName.endsWith("_" + language))
				localizedFileName += "_" + language;
		}
		InputStream instrm = getContext().getServletContext().getResourceAsStream(resourceBase + localizedFileName);

		if (null == instrm)
			instrm = getContext().getServletContext().getResourceAsStream(resourceBase + fileName);

		if (null == instrm)
			throw new FileNotFoundException("Files not found " + localizedFileName + " nor " + fileName);

		return pipeStreamToStringBuffer(instrm);
	}

	/**
	 * <p>Get value of cookie from HttpServletRequest</p>
	 * <p>Cookie value must be ISO8859_1 encoded.
	 * @param cookieName String
	 * @return String
	 */
	public String getCookie(String cookieName) {
		String cookieValue = null;
		try {
			Cookie aCookies[] = getContext().getRequest().getCookies();
			if (null != aCookies) {
				for (int c = 0; c < aCookies.length; c++) {
					if (aCookies[c].getName().equals(cookieName)) {
						cookieValue = aCookies[c].getValue();
						if (null != cookieValue)
							cookieValue = URLDecoder.decode(aCookies[c].getValue(), "ISO8859_1");
						break;
					} // fi(aCookies[c]==sName)
				} // next(c)
			} // fi
		} catch (UnsupportedEncodingException neverthrown) {
		}
		return cookieValue;
	}

	/**
	 * <p>Set value of cookie. The supplied string value will be ISO8859_1 encoded.</p>
	 * @param cookieName String
	 * @param cookieValue String
	 */
	public void setCookie(String cookieName, String cookieValue) {
		HttpServletResponse oRes = getContext().getResponse();
		oRes.addCookie(new Cookie(cookieName, cookieValue));
	}

	/**
	 * <p>Get parameter value from HttpServletRequest</p>
	 * @param paramName String Parameter name
	 * @return String
	 */
	public String getParam(String paramName) {
		HttpServletRequest httpReq = getContext().getRequest();
		String sRetVal = httpReq.getParameter(paramName);
		if (sRetVal == null)
			sRetVal = (String) httpReq.getAttribute(paramName);
		return sRetVal;
	}

	/**
	 * <p>Get parameter value from HttpServletRequest with default value</p>
	 * @param paramName String Parameter name
	 * @param defaultValue String Default value
	 * @return String
	 */
	public String getParam(String paramName, String defaultValue) {
		String retval = getParam(paramName);
		if (retval == null)
			retval = defaultValue;
		return retval;
	}

	/**
	 * <p>Set parameter value at FlashScope</p>
	 * @param sParamName String Parameter name
	 * @param sParamValue String Parameter value
	 */
	public void setParam(String sParamName, String sParamValue) {
		FlashScope oFscope = FlashScope.getCurrent(getContext().getRequest(), true);
		oFscope.put(sParamName, sParamValue);
	}

	/**
	 * <p>Concatenate attributes javax.servlet.forward.request_uri and javax.servlet.forward.path_info, then append parameter key=value pairs.</p>
	 * @return String request_uri + path_info + ? param1=value1 & param1=value1 &hellip;
	 */
	public String getLastUrl() {
		HttpServletRequest req = getContext().getRequest();
		StringBuilder sb = new StringBuilder();

		// Start with the URI and the path
		String uri = (String) req.getAttribute("javax.servlet.forward.request_uri");
		String path = (String) req.getAttribute("javax.servlet.forward.path_info");
		if (uri == null) {
			uri = req.getRequestURI();
			path = req.getPathInfo();
		}
		sb.append(uri);
		if (path != null)
			sb.append(path);

		// Now the request parameters
		Map<String, String[]> map = new HashMap<String, String[]>(req.getParameterMap());
		if (!map.isEmpty()) {
			sb.append('?');

			// Remove previous locale parameter, if present.
			// map.remove(MyLocalePicker.LOCALE);

			// Append the parameters to the URL
			for (String key : map.keySet()) {
				String[] values = map.get(key);
				for (String value : values)
					sb.append(key).append('=').append(value).append('&');
			}
			// Remove the last '&'
			sb.deleteCharAt(sb.length() - 1);			
		}

		return sb.toString();
	}

	/**
	 * <p>Get error collection as XML String like:</p>
	 * <code>&lt;errors count="1"/&gt;
	 * &lt;error field=\"field_name"&gt;Error Message&lt;/error&gt;
	 * &lt;/errors&gt;</code>
	 * @param errors Collection<SimpleError>
	 * @return String
	 */
	public String errorsToXML(Collection<SimpleError> errors) {
		StringBuilder buffer = new StringBuilder();
		if (null == errors) {
			buffer.append("<errors count=\"0\" />");
		} else {
			buffer.append("<errors count=\"").append(String.valueOf(errors.size())).append("\"");
			for (SimpleError e : errors)
				buffer.append("<error field=\"").append(e.getFieldName()).append("\">").append(e.getMessage(getContext().getLocale())).append("</error>");
			buffer.append("</errors>");
		}
		return buffer.toString();
	}

}