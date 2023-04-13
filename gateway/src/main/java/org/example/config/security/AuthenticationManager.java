package org.example.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Lazy
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final WebClient.Builder webClient;

    @Autowired
    private RequestProvider requestProvider;

    public AuthenticationManager(WebClient.Builder webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String jwtToken = authentication.getCredentials().toString();
        requestProvider.validateToken(jwtToken);
        UsernamePasswordAuthenticationToken test = new UsernamePasswordAuthenticationToken(
                null, null, Collections.singletonList(
                new SimpleGrantedAuthority("VerifiedToken")
        )
        );
        return Mono.just(test);
    }
}
