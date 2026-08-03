package br.com.csm.config;

import br.com.csm.model.User;
import br.com.csm.repository.UserRepository;
import br.com.csm.service.TokenService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Extrai o token do cabeçalho do request
        var token = this.recoverToken(request);

        if (token != null) {
            //Valida a assinatura e pega o token que esta dentro
            DecodedJWT jwt = tokenService.validateTokenAndGetClaims(token);
            if (jwt != null) {
                String login = jwt.getSubject();

                List<String> permissions = jwt.getClaim("permissions").asList(String.class);

                List<SimpleGrantedAuthority> authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                // Busca o usuario para garantir que ele ainda existe
                User user = userRepository.findByLoginAndStatusAndDeletedAtIsNull(login, 1).orElse(null);

                if (user != null) {
                    //Cria o objeto de autenticacao do spring
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

                    // Salva o usuario no contexto da requisicao
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;

        // limpa a hash do token
        return authHeader.replace("Bearer ", "").trim();
    }
}
