package com.settler.domain.auth.service;

import com.settler.domain.users.entity.User;
import java.util.UUID;

public interface RefreshTokenService {

    String generateRefreshToken(User user);

    boolean validateRefreshToken(String rawToken);

    void revoke(String rawToken);

    UUID findUserIdByRefreshToken(String rawToken);
}
