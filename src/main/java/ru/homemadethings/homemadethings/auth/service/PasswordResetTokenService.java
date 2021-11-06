/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.homemadethings.homemadethings.auth.service;

import ru.homemadethings.homemadethings.auth.exception.InvalidTokenRequestException;
import ru.homemadethings.homemadethings.auth.exception.ResourceNotFoundException;
import ru.homemadethings.homemadethings.auth.model.PasswordResetToken;
import ru.homemadethings.homemadethings.auth.model.User;
import ru.homemadethings.homemadethings.auth.model.payload.PasswordResetRequest;
import ru.homemadethings.homemadethings.auth.repository.PasswordResetTokenRepository;
import ru.homemadethings.homemadethings.auth.util.Util;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository repository;

    @Value("${app.token.password.reset.duration}")
    private Long expiration;

    @Autowired
    public PasswordResetTokenService(PasswordResetTokenRepository repository) {
        this.repository = repository;
    }

    /**
     * Finds a token in the database given its naturalId or throw an exception.
     * The reset token must match the email for the user and cannot be used again
     */
    public PasswordResetToken getValidToken(PasswordResetRequest request) {
        String tokenID = request.getToken();
        PasswordResetToken token = repository.findByToken(tokenID)
                .orElseThrow(() -> new ResourceNotFoundException("Password Reset Token", "Token Id", tokenID));

        matchEmail(token, request.getEmail());
        verifyExpiration(token);
        return token;
    }

    /**
     * Creates and returns a new password token to which a user must be
     * associated and persists in the token repository.
     */
    public Optional<PasswordResetToken> createToken(User user) {
        PasswordResetToken token = createTokenWithUser(user);
        return Optional.of(repository.save(token));
    }

    /**
     * Mark this password reset token as claimed (used by user to update password)
     * Since a user could have requested password multiple times, multiple tokens
     * would be generated. Hence, we need to invalidate all the existing password
     * reset tokens prior to changing the user password.
     */
    public PasswordResetToken claimToken(PasswordResetToken token) {
        User user = token.getUser();
        token.setClaimed(true);

        CollectionUtils.emptyIfNull(repository.findActiveTokensForUser(user))
                .forEach(t -> t.setActive(false));

        return token;
    }

    /**
     * Verify whether the token provided has expired or not on the basis of the current
     * server time and/or throw error otherwise
     */
    void verifyExpiration(PasswordResetToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new InvalidTokenRequestException("Password Reset Token", token.getToken(),
                    "Expired token. Please issue a new request");
        }
        if (!token.getActive()) {
            throw new InvalidTokenRequestException("Password Reset Token", token.getToken(),
                    "Token was marked inactive");
        }
    }

    /**
     * Match whether the token provided was actually generated by the user
     */
    void matchEmail(PasswordResetToken token, String requestEmail) {
        if (token.getUser().getEmail().compareToIgnoreCase(requestEmail) != 0) {
            throw new InvalidTokenRequestException("Password Reset Token", token.getToken(),
                    "Token is invalid for the given user " + requestEmail);
        }
    }

    PasswordResetToken createTokenWithUser(User user) {
        String tokenID = Util.generateRandomUuid();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenID);
        token.setExpiryDate(Instant.now().plusMillis(expiration));
        token.setClaimed(false);
        token.setActive(true);
        token.setUser(user);
        return token;
    }
}
