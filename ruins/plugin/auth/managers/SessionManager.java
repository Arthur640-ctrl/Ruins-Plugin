package fr.ruins.plugin.auth.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SessionManager {
    private final Set<UUID> authenticated = new HashSet<>();

    public void authenticate(UUID uuid) {
        authenticated.add(uuid);
    }

    public void logout(UUID uuid) {
        authenticated.remove(uuid);
    }

    public boolean is_authenticated(UUID uuid) {
        return authenticated.contains(uuid);
    }
}

