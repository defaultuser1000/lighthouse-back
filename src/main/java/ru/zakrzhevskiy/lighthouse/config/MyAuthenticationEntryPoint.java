package ru.zakrzhevskiy.lighthouse.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(authException.getMessage());
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("LighthouseFilmLab");
        super.afterPropertiesSet();
    }

}
