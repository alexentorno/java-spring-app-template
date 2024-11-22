package ee.api.orders.config;

import ee.api.orders.handlers.ApiAccessDeniedHandler;
import ee.api.orders.handlers.ApiEntryPoint;
import ee.api.orders.handlers.ApiLogoutSuccessHandler;
import ee.api.orders.jwt.JwtAuthenticationFilter;
import ee.api.orders.jwt.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@EnableMethodSecurity
@PropertySource("classpath:/application.properties")
public class SecurityConfig {

    @Value("${jwt.signing.key}")
    private String jwtKey;

    private final MvcRequestMatcher.Builder mvc;

    public SecurityConfig(HandlerMappingIntrospector introspector) {
        this.mvc = new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // kui ligipääs puudub (302), siis suunatakse sisselogimise vormile
        http.formLogin(withDefaults());

        http.authorizeHttpRequests(conf -> conf
                .requestMatchers(mvcMatcher("/version")).permitAll() // kõik kasutajad on siia aadressi lubatud
                .requestMatchers(mvcMatcher("/login")).permitAll() // kõik kasutajad on siia aadressi lubatud
                .requestMatchers(mvcMatcher("/users")).hasRole("ADMIN") // kasutaja ainult admin rolliga on siia aadressi lubatud
                .requestMatchers(mvcMatcher("/**")).authenticated()); // päringud siia vajavad autentimist

        http.csrf(AbstractHttpConfigurer::disable); // Sellega välja lülitakse csrf protokolli

        // Ligipääsu puudumisel ei suunata sisselogimise vormile vaid tagastatakse vea kood (401)
        http.exceptionHandling(conf -> conf
                .authenticationEntryPoint(new ApiEntryPoint())
                .accessDeniedHandler(new ApiAccessDeniedHandler()));

        // Üldine reegel
        http.with(new FilterConfigurer(), withDefaults());


        return http.build();
    }

    @Bean
    public UserDetailsService userDetailService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }


    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(false);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    public class FilterConfigurer extends AbstractHttpConfigurer<FilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) {

            AuthenticationManager manager = http.getSharedObject(AuthenticationManager.class);

            var loginFilter = new JwtAuthenticationFilter(manager, "/api/login", jwtKey);
            http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);

            var authorizationFilter = new JwtAuthorizationFilter(jwtKey);
            http.addFilterBefore(authorizationFilter, AuthorizationFilter.class);
        }
    }

    private MvcRequestMatcher mvcMatcher(String pattern) {
        return mvc.pattern(pattern);
    }
}
