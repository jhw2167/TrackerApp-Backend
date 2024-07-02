package com.jack.security;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jack.controller.ExceptionHandlingControllerAdvice;
import com.jack.utility.HttpUnitResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

@Component
public class requestThrottleFilter implements Filter {

    private int MAX_REQUESTS_PER_SECOND = 30; //or whatever you want it to be
    private int MAX_POST_REQUESTS_PER_HOUR = 10;

    private LoadingCache<String, Integer> mapRequestCountsByIpAddress;
    private LoadingCache<String, Integer> mapPostRequestCountsByIpAddress;

    public requestThrottleFilter()
    {
      super();
      /*
          Description: CacheLoader is a "functional interface" which is a fancy way of dressing up a lambda
          function that serves as the default loader for the cache.
       */
      CacheLoader<String, Integer> basicLoader = new CacheLoader<String, Integer>() {
        public Integer load(String key) { return 0; }
      };

      mapRequestCountsByIpAddress = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build(basicLoader);
      mapPostRequestCountsByIpAddress = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build(basicLoader);

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String clientIpAddress = getClientIP((HttpServletRequest) servletRequest);

        String errorMesssage = null;
        if(isMaximumRequestsPerSecondExceeded(clientIpAddress))
        {
          errorMesssage = String.format("Demo may not submit more than %d requests per second.", MAX_REQUESTS_PER_SECOND);
        }

        if( httpServletRequest.getMethod().equals("POST") )
        {
            if(isMaximumPostRequestsPerHourExceeded(clientIpAddress))
            {
                errorMesssage = String.format("Demo may not submit more than %d POST requests per hour.", MAX_POST_REQUESTS_PER_HOUR);
            }
        }

        if(errorMesssage != null)
        {

            String jsonError = ExceptionHandlingControllerAdvice.createJson("Too many Requests", errorMesssage);
            httpServletResponse.getWriter().write(jsonError);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isMaximumRequestsPerSecondExceeded(String clientIpAddress)
    {
      Integer requests = 0;
      requests = mapRequestCountsByIpAddress.get(clientIpAddress);
      if(requests != null)
      {
          if(requests > MAX_REQUESTS_PER_SECOND)
          {
            mapRequestCountsByIpAddress.asMap().remove(clientIpAddress);
            mapRequestCountsByIpAddress.put(clientIpAddress, requests);
            return true;
          }

      }
      else
      {
        requests = 0;
      }
      requests++;
      mapRequestCountsByIpAddress.put(clientIpAddress, requests);
      return false;
      }

      private boolean isMaximumPostRequestsPerHourExceeded(String clientIpAddress)
      {
        Integer requests = 0;
        requests = mapPostRequestCountsByIpAddress.get(clientIpAddress);
        if(requests != null)
        {
            if(requests > MAX_POST_REQUESTS_PER_HOUR)
            {
              mapPostRequestCountsByIpAddress.asMap().remove(clientIpAddress);
              mapPostRequestCountsByIpAddress.put(clientIpAddress, requests);
              return true;
            }

        }
        else
        {
          requests = 0;
        }
        requests++;
        mapPostRequestCountsByIpAddress.put(clientIpAddress, requests);
        return false;
        }

    public String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Override
    public void destroy() {

    }
}