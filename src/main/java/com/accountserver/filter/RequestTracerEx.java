package com.accountserver.filter;

import com.webcore.filter.RequestTracer;

import javax.servlet.annotation.WebFilter;

/**
 * Register request tracer.
 */
@WebFilter(
		filterName = "RequestTracer",
		urlPatterns = {"/*"}
)
public class RequestTracerEx extends RequestTracer {
}
