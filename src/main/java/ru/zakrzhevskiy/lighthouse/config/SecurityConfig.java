package ru.zakrzhevskiy.lighthouse.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private MyAuthenticationEntryPoint authEntryPoint;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .authoritiesByUsernameQuery(
                        "select u.username, r.name " +
                                "from users u " +
                                "join users_roles ur on ur.user_id = u.id " +
                                "join roles r on ur.role_id = r.id " +
                                "where u.username=?"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/authenticate",
                        "/users/sign-up",
                        "/users/registrationConfirm",
                        "/users/setUserDetails",
                        "/activationSuccess",
                        "/errorActivating"
                ).anonymous()
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().fullyAuthenticated()
                .and().httpBasic()
                .authenticationEntryPoint(authEntryPoint);

        http.logout(logout -> logout.logoutUrl("/users/logout")
                .invalidateHttpSession(true)
                .deleteCookies("SESSION"))
                .cors();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    // Configuring part for Pageable serialization
    @Bean
    public Module jacksonPageWithJsonViewModule() {
        SimpleModule module = new SimpleModule("jackson-page-with-jsonview", Version.unknownVersion());
        module.addSerializer(PageImpl.class, new PageSerializer());
        return module;
    }

}
