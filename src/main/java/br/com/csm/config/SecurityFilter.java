package br.com.csm.config;

import br.com.csm.model.User;
import br.com.csm.repository.UserRepository;
import br.com.csm.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;

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
            var login = tokenService.validateToken(token);
            if (!login.isEmpty()) {
                // Busca o usuario para garantir que ele ainda existe
                User user = userRepository.findActiveAndUnblockedUser(login, 1, OffsetDateTime.now()).orElse(null);

                if (user != null) {
                    //Cria o objeto de autenticacao do spring
                    //Todo substituir o collections.emptylist por Roles
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

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
