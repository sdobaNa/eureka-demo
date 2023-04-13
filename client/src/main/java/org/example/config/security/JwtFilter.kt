package org.example.config.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import java.util.*

class JwtFilter(
    private var requestProvider: RequestProvider,
    private val adminToken: String,
) : OncePerRequestFilter() {

    // 30 полей * 400 строк * 2 (на всякий) * 2 (размер одного символа) - 39 (начальное значение)
    private val requestLengthSize: Int = 47_961

    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(httpServletRequest)
        if (httpServletRequest.requestURI.startsWith("/actuator"))
            if (httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION) == "Bearer $adminToken")
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    null, null, Collections.singletonList(
                        SimpleGrantedAuthority("ActuatorAdmin")
                    )
                )
            else {
                httpServletResponse.sendError(UNAUTHORIZED.value(), UNAUTHORIZED.reasonPhrase)
                return
            }
        else {
                val token: String? = requestProvider.resolveToken(httpServletRequest)
                if (requestProvider.validateToken(token)) {
                    SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                        null, null, Collections.singletonList(
                            SimpleGrantedAuthority("VerifiedToken")
                        )
                    )
                }
                filterChain.doFilter(wrappedRequest, httpServletResponse)
        }
    }
}