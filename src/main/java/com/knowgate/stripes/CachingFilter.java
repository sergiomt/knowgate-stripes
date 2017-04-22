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
 * <p>Set Expires HTTP header value.</p>
 * <p>Expires HTTP header will be set as specified in milliseconds at expires FilterConfig parameter. Default value is seven days.</p>
 * <p>Install at web.xml with:</p>
 * <code>&lt;filter&gt;
 * &nbsp;&nbsp;&lt;filter-name&gt;Caching Filter&lt;/filter-name&gt;
 * &nbsp;&nbsp;&lt;filter-class&gt;com.knowgate.stripes.CachingFilter&lt;/filter-class&gt;
 * &nbsp;&nbsp;&lt;init-param&gt;
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;expires&lt;/param-name&gt;
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;604800000&lt;/param-value&gt;
 * &nbsp;&nbsp;&lt;/init-param&gt;
 * &lt;/filter&gt;</code>
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class CachingFilter implements Filter {

	private final static long ONE_WEEK = 604800000L;

	private long timeout;
    
    /**
     * @param FilterConfig Contains "expires" init parameter with timeout in milliseconds
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
      String expires = filterConfig.getInitParameter("expires");
      if (null==expires) {
    	  timeout = ONE_WEEK;
      } else {
    	  try {
    		  timeout = Long.parseLong(expires);
    	  } catch (NumberFormatException xcpt) {
        	  timeout = ONE_WEEK;    		  
    	  }
      }
    }

    /**
     * @throws IOException
     * @throws ServletException
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setDateHeader("Expires", System.currentTimeMillis() + timeout);
      chain.doFilter(request, response);
    }

    /**
    * @see javax.servlet.Filter#destroy()
    */
    @Override
    public void destroy() { }

}