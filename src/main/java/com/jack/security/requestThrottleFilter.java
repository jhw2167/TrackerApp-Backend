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

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

@Component
public class requestThrottleFilter implements Filter {

    private int MAX_REQUESTS_PER_SECOND = 10; //or whatever you want it to be

    private LoadingCache<String, Integer> requestCountsPerIpAddress;

    public requestThrottleFilter(){
      super();
      requestCountsPerIpAddress = Caffeine.newBuilder().
            expireAfterWrite(1, TimeUnit.SECONDS).build(new CacheLoader<String, Integer>() {
        public Integer load(String key) {
            return 0;
        }
    });
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
        if(isMaximumRequestsPerSecondExceeded(clientIpAddress)){
          httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
          httpServletResponse.getWriter().write("Too many requests");
          return;
         }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isMaximumRequestsPerSecondExceeded(String clientIpAddress){
      Integer requests = 0;
      requests = requestCountsPerIpAddress.get(clientIpAddress);
      if(requests != null){
          if(requests > MAX_REQUESTS_PER_SECOND) {
            requestCountsPerIpAddress.asMap().remove(clientIpAddress);
            requestCountsPerIpAddress.put(clientIpAddress, requests);
            return true;
        }

      } else {
        requests = 0;
      }
      requests++;
      requestCountsPerIpAddress.put(clientIpAddress, requests);
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