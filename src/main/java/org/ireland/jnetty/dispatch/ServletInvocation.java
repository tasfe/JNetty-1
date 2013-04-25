/*
 * Copyright (c) 1998-2012 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package org.ireland.jnetty.dispatch;

import javax.servlet.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A repository for request information gleaned from the uri.
 * 
 * and the FilterChain that match the URI
 * 
 * A Invocation include the URI information and the FilterChain that match the URI
 */
public class ServletInvocation
{
	private static final Log log = LogFactory.getLog(ServletInvocation.class.getName());

	private static final boolean debug = log.isDebugEnabled();

	private ClassLoader _classLoader;

	private String _contextPath = "";

	private String _contextUri;
	private String _servletPath;
	private String _pathInfo;

	private String _queryString;

	private String _servletName;
	private FilterChain _filterChain;

	private boolean _isAsyncSupported = true;
	private MultipartConfigElement _multipartConfig;

	private AtomicLong _requestCount = new AtomicLong();

	/**
	 * Creates a new invocation
	 */
	public ServletInvocation()
	{
		_classLoader = Thread.currentThread().getContextClassLoader();

	}

	/**
	 * Returns the mapped context-path.
	 */
	public final String getContextPath()
	{
		return _contextPath;
	}

	/**
	 * Sets the context-path.
	 */
	public void setContextPath(String path)
	{
		_contextPath = path;
	}

	public void setContextURI(String contextURI)
	{
		_contextUri = contextURI;
		_servletPath = contextURI;
	}

	/**
	 * Returns the URI tail, i.e. everything after the context path.
	 * 
	 * e.g:<br>
	 * 
	 * URI: "/myapp/blog/page1.jsp"<br>
	 * 
	 * contextPath: "/myapp"<br>
	 * 
	 * servletPath: "/blog"
	 * 
	 * pathInfo:    "/page1.jsp"
	 * 
	 * ContextURI:  "/blog/page1.jsp"<br>
	 * 
	 *
	 * 
	 */
	public final String getContextURI()
	{
		return _contextUri;
	}

	/**
	 * Returns the mapped servlet path.
	 */
	public final String getServletPath()
	{
		return _servletPath;
	}

	/**
	 * Sets the mapped servlet path.
	 */
	public void setServletPath(String servletPath)
	{
		_servletPath = servletPath;
	}

	/**
	 * Returns the mapped path info.
	 */
	public final String getPathInfo()
	{
		return _pathInfo;
	}

	/**
	 * Sets the mapped path info
	 */
	public void setPathInfo(String pathInfo)
	{
		_pathInfo = pathInfo;
	}

	/**
	 * Returns the query string. Characters remain unescaped.
	 */
	public final String getQueryString()
	{
		return _queryString;
	}

	/**
	 * Returns the query string. Characters remain unescaped.
	 */
	public final void setQueryString(String queryString)
	{
		_queryString = queryString;
	}

	/**
	 * Sets the class loader.
	 */
	public void setClassLoader(ClassLoader loader)
	{
		_classLoader = loader;
	}

	/**
	 * Gets the class loader.
	 */
	public ClassLoader getClassLoader()
	{
		return _classLoader;
	}

	/**
	 * Sets the servlet name
	 */
	public void setServletName(String servletName)
	{
		_servletName = servletName;
	}

	/**
	 * Gets the servlet name
	 */
	public String getServletName()
	{
		return _servletName;
	}

	/**
	 * Sets the filter chain
	 */
	public void setFilterChain(FilterChain chain)
	{
		_filterChain = chain;
	}

	/**
	 * Gets the filter chain
	 */
	public FilterChain getFilterChain()
	{
		return _filterChain;
	}

	/**
	 * Returns the number of requests.
	 */
	public long getRequestCount()
	{
		return _requestCount.get();
	}

	/**
	 * True if the invocation chain supports async (comet) requets.
	 */
	public boolean isAsyncSupported()
	{
		return _isAsyncSupported;
	}

	/**
	 * Mark the invocation chain as not supporting async.
	 */
	public void clearAsyncSupported()
	{
		_isAsyncSupported = false;
	}

	public MultipartConfigElement getMultipartConfig()
	{
		return _multipartConfig;
	}

	public void setMultipartConfig(MultipartConfigElement multipartConfig)
	{
		_multipartConfig = multipartConfig;
	}



	/**
	 * Service a request.
	 * 
	 * @param request
	 *            the servlet request
	 * @param response
	 *            the servlet response
	 */
	public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException
	{
		_requestCount.incrementAndGet();

		if (debug)
			log.debug("Dispatch '" + _contextUri + "' to " + _filterChain);

		_filterChain.doFilter(request, response);
	}

	/**
	 * Copies from the invocation.
	 */
	public void copyFrom(ServletInvocation invocation)
	{
		_classLoader = invocation._classLoader;
		_contextPath = invocation._contextPath;

		_contextUri = invocation._contextUri;
		_servletPath = invocation._servletPath;
		_pathInfo = invocation._pathInfo;

		_queryString = invocation._queryString;

		_servletName = invocation._servletName;
		_filterChain = invocation._filterChain;

	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append(_contextUri);

		if (_queryString != null)
			sb.append("?").append(_queryString);

		sb.append("]");

		return sb.toString();
	}
}
