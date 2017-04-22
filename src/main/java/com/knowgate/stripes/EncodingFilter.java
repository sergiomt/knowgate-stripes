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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Set character encoding and no-cache headers</p>
 * <p>Headers will be set as: Expires=0, Cache-Control=no-cache and Pragma=no-cache</p>
 * <p>Install at web.xml with:</p>
 * <code>&lt;filter&gt;
 * &nbsp;&nbsp;&lt;filter-name&gt;Encoding Filter&lt;/filter-name&gt;
 * &nbsp;&nbsp;&lt;filter-class&gt;com.knowgate.stripes.EncodingFilter&lt;/filter-class&gt;
 * &nbsp;&nbsp;&lt;init-param&gt;
 * &nbsp;&nbsp;&lt;param-name&gt;encoding&lt;/param-name&gt;
 * &nbsp;&nbsp;&lt;param-value&gt;UTF-8&lt;/param-value&gt;
 * &nbsp;&nbsp;&lt;/init-param&gt;
 * &lt;/filter&gt;</code>
 * @author Sergio Montoro Ten
 * @version 1.0
*/
public class EncodingFilter implements Filter {

private String encoding;
private FilterConfig filterConfig;

/**
 * @param FilterConfig Contains "encoding" init parameter with a valid character encoding name. Default is UTF-8.
 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
*/
@Override
public void init(FilterConfig fc) throws ServletException {
  filterConfig = fc;
  encoding = filterConfig.getInitParameter("encoding");
  if (null==encoding)
	  encoding = "UTF-8";
  else if (encoding.trim().length()==0)
	  encoding = "UTF-8";
}

/**
 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
*/
@Override
public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
  req.setCharacterEncoding(encoding);
  HttpServletResponse httpResponse = (HttpServletResponse) resp;
  httpResponse.setDateHeader("Expires", 0);
  httpResponse.setHeader("Cache-Control", "no-cache");
  httpResponse.setHeader("Pragma", "no-cache");  
  chain.doFilter(req, resp);
}

/**
 * @see javax.servlet.Filter#destroy()
*/
@Override
public void destroy() { }

}