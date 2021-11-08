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
package ru.homemadethings.homemadethings.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javassist.NotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.homemadethings.homemadethings.auth.annotation.CurrentUser;
import ru.homemadethings.homemadethings.auth.event.OnUserAccountChangeEvent;
import ru.homemadethings.homemadethings.auth.event.OnUserLogoutSuccessEvent;
import ru.homemadethings.homemadethings.auth.exception.UpdatePasswordException;
import ru.homemadethings.homemadethings.auth.model.CustomUserDetails;
import ru.homemadethings.homemadethings.auth.model.payload.*;
import ru.homemadethings.homemadethings.auth.service.AuthService;
import ru.homemadethings.homemadethings.auth.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@Api(value = "User Rest API", description = "Defines endpoints for the logged in user. It's secured by default")

public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    private final AuthService authService;

    private final UserService userService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserController(AuthService authService, UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.authService = authService;
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Gets the current user profile of the logged in user
     */
    @GetMapping("/me")
    @ApiOperation(value = "Returns the current user profile")
    public void getUserProfile() {

    }

    /**
     * Returns all admins in the system. Requires Admin access
     */
    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Returns the list of configured admins. Requires ADMIN Access")
    public ResponseEntity getAllAdmins() {
        logger.info("Inside secured resource with admin");
        return ResponseEntity.ok("Hello. This is about admins");
    }

    /**
     * Updates the password of the current logged in user
     */
    @PostMapping("/password/update")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Allows the user to change his password once logged in by supplying the correct current " +
            "password")
    public ResponseEntity updateUserPassword(@CurrentUser CustomUserDetails customUserDetails,
                                             @ApiParam(value = "The UpdatePasswordRequest payload") @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {

        return authService.updatePassword(customUserDetails, updatePasswordRequest)
                .map(updatedUser -> {
                    OnUserAccountChangeEvent onUserPasswordChangeEvent = new OnUserAccountChangeEvent(updatedUser, "Update Password", "Change successful");
                    applicationEventPublisher.publishEvent(onUserPasswordChangeEvent);
                    return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
                })
                .orElseThrow(() -> new UpdatePasswordException("--Empty--", "No such user present."));
    }

    /**
     * Log the user out from the app/device. Release the refresh token associated with the
     * user device.
     */
    @PostMapping("/logout")
    @ApiOperation(value = "Logs the specified user device and clears the refresh tokens associated with it")
    public ResponseEntity logoutUser(@CurrentUser CustomUserDetails customUserDetails,
                                     @ApiParam(value = "The LogOutRequest payload") @Valid @RequestBody LogOutRequest logOutRequest) {
        userService.logoutUser(customUserDetails, logOutRequest);
        Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();

        OnUserLogoutSuccessEvent logoutSuccessEvent = new OnUserLogoutSuccessEvent(customUserDetails.getEmail(), credentials.toString(), logOutRequest);
        applicationEventPublisher.publishEvent(logoutSuccessEvent);
        return ResponseEntity.ok(new ApiResponse(true, "Log out successful"));
    }

    @PutMapping("/first-name")
    public ResponseEntity<ApiResponse> updateFirstName(@CurrentUser CustomUserDetails customUserDetails, @RequestBody UpdateFirstNameRequest updateFirstNameRequest) throws NotFoundException {
        return userService
                .updateFirstName(customUserDetails, updateFirstNameRequest.getFirstName())
                .map(user -> {
                    logger.info("Update firstName with user id=" + user.getId() + " is successful");
                    return ResponseEntity.ok(new ApiResponse(true, "Update firstName successful"));
                })
                .orElseThrow(() -> new RuntimeException("Update firstName exception"));
    }

    @PutMapping("/last-name")
    public ResponseEntity<ApiResponse> updateLastName(@CurrentUser CustomUserDetails customUserDetails, @RequestBody UpdateLastNameRequest updateLastNameRequest) throws NotFoundException {
        return userService
                .updateLastName(customUserDetails, updateLastNameRequest.getLastName())
                .map(user -> {
                    logger.info("Update lastName with user id=" + user.getId() + " is successful");
                    return ResponseEntity.ok(new ApiResponse(true, "Update lastName successful"));
                })
                .orElseThrow(() -> new RuntimeException("Update lastName exception"));
    }
}
