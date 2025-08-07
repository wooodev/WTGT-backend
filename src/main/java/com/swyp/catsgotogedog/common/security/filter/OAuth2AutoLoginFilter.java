package com.swyp.catsgotogedog.common.security.filter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2AutoLoginFilter extends OncePerRequestFilter {

	public final static String AUTO_LOGIN_PARAM = "autoLogin";

	@Value("${frontend.base.url}")
	private String frontend_base_url;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		if(request.getRequestURI().contains("/oauth2/authorization/")) {
			String autoLoginParam = request.getParameter(AUTO_LOGIN_PARAM);

			String targetUrl = "";
			String refererHeader = request.getHeader("Referer");
			log.info("refererHeader :: {} ", refererHeader);
			URL parsedUrl = null;


			if(autoLoginParam == null) {
				autoLoginParam = "false";
			}

			boolean autoLogin = "true".equalsIgnoreCase(autoLoginParam);

			// Referer 헤더를 통해 Redirect
			if (refererHeader != null && !refererHeader.isEmpty()) {
				try {
					parsedUrl = new URL(refererHeader);
					if (parsedUrl.getHost() != null && !parsedUrl.getHost().isEmpty()) {
						UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
							.scheme(parsedUrl.getProtocol())
							.host(parsedUrl.getHost());
						if (parsedUrl.getPort() != -1) {
							builder.port(parsedUrl.getPort());
						}
						targetUrl = builder.path("/authredirect").build().toUriString();
						log.info("Referer 헤더를 통한 Redirect :: {}", targetUrl);
					}
				} catch (MalformedURLException e) {
					log.warn("잘못된 형식의 URL :: {}", refererHeader, e);
				}
			}

			String requestURLString = request.getRequestURL().toString();

			try {
				URL requestURL = new URL(requestURLString);
				String host = requestURL.getHost();
				String scheme = requestURL.getProtocol();
				int port = requestURL.getPort();

				if (host != null && (host.equals("localhost") || host.equals("127.0.0.1"))) {
					if (frontend_base_url != null && !frontend_base_url.isEmpty()) {
						// Use the specifically configured localhost frontend URL
						targetUrl = UriComponentsBuilder.fromUriString(frontend_base_url)
							.path("/authredirect")
							.build()
							.toUriString();
						log.info("Referer Header가 존재하지 않아 frontend_base_url로 redirect :: {}", targetUrl);
					} else {
						UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
							.scheme(scheme)
							.host(host);
						if (port != -1) {
							builder.port(port);
						}
						targetUrl = builder.path("/authredirect").build().toUriString();
						log.info("frontend_base_url이 존재하지 않아 request url로 강제 redirect :: {}", targetUrl);
					}
				}
			} catch (MalformedURLException e) {
				log.warn("잘못된 형식의 비 Referer URL :: {}", requestURLString, e);
			}
			
			HttpSession session = request.getSession(true);
			session.setAttribute(AUTO_LOGIN_PARAM, autoLogin);
			session.setAttribute("targetUrl", targetUrl);
		}

		filterChain.doFilter(request, response);
	}
}
