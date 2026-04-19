package com.kvanzi.jwtauth.auth.internal.properties;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.Cookie.SameSite;

import javax.crypto.SecretKey;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private @NonNull String secretKey;

    @Setter(AccessLevel.NONE)
    private SecretKey signingKey;

    private final Access access = new Access();
    private final Refresh refresh = new Refresh();
    private final Cookie cookie = new Cookie();

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }


    @Getter
    @Setter
    public static class Refresh {

        private int duration;
        private @NonNull ChronoUnit durationUnit;
    }

    @Getter
    @Setter
    public static class Access {

        private int duration;
        private @NonNull ChronoUnit durationUnit;
    }

    @Getter
    @Setter
    public static class Cookie {

        private boolean secure;
        private boolean httpOnly;
        private @NonNull SameSite sameSite;
        private @NonNull String domain;
    }
}
