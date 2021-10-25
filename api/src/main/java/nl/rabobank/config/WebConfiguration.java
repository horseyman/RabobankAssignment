package nl.rabobank.config;

import nl.rabobank.converter.AttorneyConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 */

@Configuration
public class WebConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .authorizeRequests(a -> a.anyRequest().permitAll())
          .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new AttorneyConverter());
    }
}
