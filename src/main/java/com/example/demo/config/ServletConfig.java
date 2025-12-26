// package com.example.demo.config;

// import com.example.demo.security.JwtAuthenticationFilter;
// import com.example.demo.security.JwtTokenProvider;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import java.util.List;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     /* =========================
//        JWT TOKEN PROVIDER BEAN
//        ========================= */
//     @Bean
//     public JwtTokenProvider jwtTokenProvider() {
//         return new JwtTokenProvider();
//     }

//     /* =========================
//        JWT FILTER BEAN
//        ========================= */
//     @Bean
//     public JwtAuthenticationFilter jwtAuthenticationFilter(
//             JwtTokenProvider jwtTokenProvider
//     ) {
//         return new JwtAuthenticationFilter(jwtTokenProvider);
//     }

//     /* =========================
//        CORS CONFIGURATION (FIXES SWAGGER ERROR)
//        ========================= */
//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {

//         CorsConfiguration configuration = new CorsConfiguration();
//         configuration.setAllowedOrigins(List.of("*"));
//         configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//         configuration.setAllowedHeaders(List.of("*"));

//         UrlBasedCorsConfigurationSource source =
//                 new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);

//         return source;
//     }

//     /* =========================
//        SECURITY FILTER CHAIN
//        ========================= */
//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//         http
//             .cors(cors -> {})               // ✅ ENABLE CORS
//             .csrf(csrf -> csrf.disable())
//             .sessionManagement(session ->
//                 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//             )
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers(
//                         "/auth/**",
//                         "/hello-servlet",
//                         "/swagger-ui/**",
//                         "/swagger-ui.html",
//                         "/v3/api-docs/**"
//                 ).permitAll()
//                 .anyRequest().permitAll()   // ✅ allow API calls for testing
//             );

//         return http.build();
//     }
// }
// // 