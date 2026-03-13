package org.example.boxlybackend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final JwtConverterProperties properties;


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        log.info("Converting Jwt to AbstractAuthenticationToken");
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
        String principalClaimName = getPrincipalClaimName(jwt);
        System.out.println("principalClaimName: " + principalClaimName);
        log.info("Converting Jwt to AbstractAuthenticationToken {}",principalClaimName);
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        log.info("getPrincipalClaimName: {}", properties.getPrincipalAttribute());
        if (properties.getPrincipalAttribute() != null) {
            claimName = properties.getPrincipalAttribute();
        }
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) {
            return Set.of();
        }

        // Create a collection to hold all roles from different resources
        Set<String> allRoles = new HashSet<>();

        // Loop through each resource in resource_access
        for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
            // For each resource, get the roles
            Map<String, Object> resource = (Map<String, Object>) entry.getValue();
            Collection<String> resourceRoles = (Collection<String>) resource.get("roles");

            // If roles exist, add them to the allRoles set
            if (resourceRoles != null) {
                allRoles.addAll(resourceRoles);
            }
        }
        log.info("ResourceAccess roles: {}", allRoles);
        // Map the roles to SimpleGrantedAuthority
        return allRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

}