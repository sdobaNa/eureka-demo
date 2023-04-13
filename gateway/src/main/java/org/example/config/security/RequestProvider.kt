package org.example.config.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Component
class RequestProvider(
) {

    @Value("\${security.jwt.token.secret-key}")
    lateinit var secretKey: String

    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }


    fun validateToken(token: String?): Boolean {
        if (token.isNullOrBlank()) {
            throw ResponseStatusException(UNAUTHORIZED, "Expired or invalid JWT token")
        }
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            true
        } catch (e: JwtException) {
            throw ResponseStatusException(UNAUTHORIZED, "Expired or invalid JWT token")
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(INTERNAL_SERVER_ERROR, "Error 500")
        }
    }
}