package com.swyp.catsgotogedog.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.catsgotogedog.common.util.JwtTokenUtil;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter implements Filter {

    private final JwtTokenUtil jwt;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String bearer = request.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            try {
                String token = bearer.substring(7);
                String sub   = jwt.getSubject(token);

                var auth = new UsernamePasswordAuthenticationToken(
                    sub, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ExpiredJwtException e) {
                log.info("토큰이 만료되었습니다: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다.");
                return;
            } catch (MalformedJwtException e) {
                log.warn("잘못된 토큰 형식입니다: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "잘못된 토큰 형식입니다.");
                return;
            } catch (Exception e) {
                log.error("인증 처리 중 오류 발생: {}", e.getMessage(), e);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 처리 중 오류가 발생했습니다.");
                return;
            }
        }
        chain.doFilter(req, res);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.setContentType("application/json");

        CatsgotogedogApiResponse<Object> errorResponse = CatsgotogedogApiResponse.fail(status, message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}