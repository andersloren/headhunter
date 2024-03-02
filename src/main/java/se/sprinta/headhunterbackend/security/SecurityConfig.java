package se.sprinta.headhunterbackend.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class SecurityConfig {

    private final RSAPublicKey publicKey;

    private final RSAPrivateKey privateKey;

    @Value("${api.endpoint.base-url-users}")
    private String baseUrlUsers;
    @Value("${api.endpoint.base-url-jobs}")
    private String baseUrlJobs;
    @Value("${api.endpoint.base-url-ads}")
    private String baseUrlAds;
    private final AuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;



    /*
     * RSA is an algorithm.
     * The NoSuchAlgorithmException is there in case the algorithm is not supported.
     *
     * When SecurityConfiguration is instantiated, the two keys will be created immediately.
     * This means that every time this program is restarted, we have two new keys.
     * */

    public SecurityConfig(
            AuthenticationEntryPoint customBasicAuthenticationEntryPoint,
            CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint,
            CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler
    ) throws NoSuchAlgorithmException {
        this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
        this.customBearerTokenAuthenticationEntryPoint = customBearerTokenAuthenticationEntryPoint;
        this.customBearerTokenAccessDeniedHandler = customBearerTokenAccessDeniedHandler;

        // Generate a public/private key pair.
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        keyPairGenerator.initialize(2048); // The generated key will have a size of 2048 bits.
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.publicKey = (RSAPublicKey) keyPair.getPublic(); // getPublic() returns of type PublicKey class, so it must be cast to RSAPublicKey
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate(); // Same as above.
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeHttpRequest -> authorizeHttpRequest
                                .requestMatchers(HttpMethod.GET, this.baseUrlUsers + "/findAll").hasAuthority("ROLE_admin") // Find all
                                .requestMatchers(HttpMethod.GET, this.baseUrlUsers + "/findUser/{email}").hasAuthority("ROLE_admin") //
                                .requestMatchers(HttpMethod.POST, this.baseUrlUsers + "/register").permitAll()
                                .requestMatchers(HttpMethod.POST, this.baseUrlUsers + "/addUser").hasAuthority("ROLE_admin")
                                .requestMatchers(HttpMethod.PUT, this.baseUrlUsers + "/update/{email}").hasAuthority("ROLE_admin")
                                .requestMatchers(HttpMethod.DELETE, this.baseUrlUsers + "/delete/{email}").hasAuthority("ROLE_admin")
//                        .requestMatchers(HttpMethod.DELETE, this.baseUrlJobs + "/delete").hasAuthority("ROLE_admin")
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher(this.baseUrlJobs + "/**")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher(this.baseUrlAds + "/**")).permitAll()

                                // Disallow everything else
                                .anyRequest().authenticated() // Always a good idea to put this as last
                )
                .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable()) // This is for H2 browser console access
                .csrf(csrf -> csrf.disable()) // If not turned off, there will be problems when sending POST or PUT to server, resulting in 401.
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(this.customBasicAuthenticationEntryPoint))
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(this.customBearerTokenAuthenticationEntryPoint)
                        .accessDeniedHandler(this.customBearerTokenAccessDeniedHandler))
                .sessionManagement(sessionsManagement -> sessionsManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwkSet = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        /*
        Let’s say that that your authorization server communicates authorities in a custom claim called "authorities".
        In that case, you can configure the claim that JwtAuthenticationConverter should inspect, like so:
         */
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        /*
        You can also configure the authority prefix to be different as well. The default one is "SCOPE_".
        In this project, you need to change it to empty, that is, no prefix!
         */
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); // If the prefix is not changed, it would add "SCOPE_" before "ROLE_" and mess everything upp

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
