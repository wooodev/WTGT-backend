package com.swyp.catsgotogedog.common.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;
import com.swyp.catsgotogedog.global.exception.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatsgotogedogAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		CatsgotogedogApiResponse<?> apiResponse = CatsgotogedogApiResponse.fail(ErrorCode.UNAUTHORIZED_ACCESS);

		log.info(authException.getMessage(), ErrorCode.UNAUTHORIZED_ACCESS.getMessage());
		objectMapper.writeValue(response.getWriter(), apiResponse);
	}
}
