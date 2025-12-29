package se.kth.journalsystem.messageservice.util;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

public class SecurityUtils {

    public static UserInfo getUserFromJwt(Jwt jwt) {
        if (jwt == null) {
            return null;
        }

        String userId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String role = "patient"; // Default

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            if (roles.contains("admin"))
                role = "admin";
            else if (roles.contains("doctor"))
                role = "doctor";
            else if (roles.contains("staff"))
                role = "staff";
            else if (roles.contains("patient"))
                role = "patient";
        }

        return new UserInfo(userId, username, role);
    }

    public record UserInfo(String userId, String username, String role) {
    }
}
