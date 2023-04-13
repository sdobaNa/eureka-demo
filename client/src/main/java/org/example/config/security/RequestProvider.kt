package org.example.config.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import jakarta.servlet.http.HttpServletRequest
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Component
import java.util.*
import jakarta.annotation.PostConstruct
import org.springframework.web.server.ResponseStatusException

@Component
class RequestProvider(
) {
    @Value("\${security.jwt.token.secret-key}")
    private lateinit var secretKey: String

    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else if (req.getParameter("jwt") != null && req.getParameter("jwt").isNotBlank()) {
            req.getParameter("jwt")
        } else null
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