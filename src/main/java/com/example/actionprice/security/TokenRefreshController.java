package com.example.actionprice.security;

import com.example.actionprice.exception.AccessTokenException;
import com.example.actionprice.exception.RefreshTokenException;
import com.example.actionprice.security.jwt.accessToken.AccessTokenService;
import com.example.actionprice.security.jwt.refreshToken.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Log4j2
@RequiredArgsConstructor
public class TokenRefreshController {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/refresh")
    public String tokenRefresh(@RequestHeader("Authorizaion") String access_token) {
        try{
            accessTokenService.validateAccessTokenAndExtractUsername_strictly(access_token);

            return access_token;
        } catch (ExpiredJwtException e) {
            String username = e.getClaims().getSubject();

            refreshTokenService.checkRefreshFirst(username);

            String newAccessToken = accessTokenService.issueAccessToken(username);

            return newAccessToken;
        } catch (AccessTokenException e) {
            throw e;
        } catch (RefreshTokenException e) {
            throw e;
        }
    }
}
