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

import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;

import java.util.AbstractMap.SimpleImmutableEntry;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;

/**
 * <p>Base class for beans serving a response to AJAX requests</p>
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public abstract class BaseAjaxBean extends BaseStripesBean {

	private String template;
	private ArrayList<SimpleError> infos = new ArrayList<SimpleError>();
	private ArrayList<SimpleError> errors = new ArrayList<SimpleError>();	
	private ArrayList<SimpleImmutableEntry<String,String>> datavalues = new ArrayList<SimpleImmutableEntry<String,String>>();	

	public BaseAjaxBean(String template) {
		this.template = template;
	}

	public BaseAjaxBean(String template, String resourceBasepath, String resourceBundleName) {
		super(resourceBasepath, resourceBundleName);
		this.template = template;
	}

	public List<SimpleImmutableEntry<String,String>> getResponseData() {
		return datavalues;
	}
	
	public List<SimpleError> getInformationMessages() {
		return infos;
	}

	public int getInformationMessagesCount() {
		return infos.size();
	}
	
	public List<SimpleError> getErrors() {
		return errors;
	}

	public int getErrorsCount() {
		return errors.size();
	}

	public ValidationError addError(String f, SimpleError e, ValidationErrors v) {
		errors.add(e);
		if (v!=null)
			if (f!=null)
				if (f.length()>0)
					v.add(f, e);
				else
					v.addGlobalError(e);
			else
				v.addGlobalError(e);
		return e;
	}
	
	public ValidationError addError(String f, SimpleError e) {
		return addError(f, e, null);
	}

	public ValidationError addError(SimpleError e, ValidationErrors v) {
		return addError(e.getFieldName(), e, null);
	}
	
	public ValidationError addError(SimpleError e) {
		return addError(e.getFieldName(), e, null);
	}

	public ValidationError addError(String f, LocalizableError l, ValidationErrors v) {
		SimpleError e = new SimpleError(l.getMessage(getContext().getLocale()), l.getReplacementParameters());
		if (l.getFieldName()!=null) e.setFieldName(l.getFieldName());
		if (l.getFieldValue()!=null) e.setFieldValue(l.getFieldValue());
		return addError(f, e, v);
	}
	
	public ValidationError addError(LocalizableError l, ValidationErrors v) {
		SimpleError e = new SimpleError(l.getMessage(getContext().getLocale()), l.getReplacementParameters());
		if (l.getFieldName()!=null) e.setFieldName(l.getFieldName());
		if (l.getFieldValue()!=null) e.setFieldValue(l.getFieldValue());
		return addError(e, v);
	}
	
	public ValidationError addError(LocalizableError l) {
		return addError(l, null);
	}
	
	public ValidationError addInformationMessage(SimpleError m) {
		infos.add(m);
		return m;
	}

	public Entry<String,String> addDataLine(String sCode, Object oText) {
		String sText;
		if (oText==null)
			sText = "";
		else if (oText instanceof String)
			sText = (String) oText;
		else
			sText = oText.toString();
		SimpleImmutableEntry<String,String> nv = new SimpleImmutableEntry<String,String>(sCode,sText);
		datavalues.add(nv);
		return nv;
	}

	public Resolution AjaxResponseResolution() {
	    return new ForwardResolution(template);
	}

}
