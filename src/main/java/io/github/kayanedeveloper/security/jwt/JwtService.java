package io.github.kayanedeveloper.security.jwt;

import io.github.kayanedeveloper.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.expiracao}")
    private String expiracao;
    @Value("${security.jwt.chave-assinatura}")
    private String chaveAssinatura;

    public String gerarToken(Usuario usuario) {
        final var expString = Long.valueOf(expiracao);
        final var dataHoraExpiracao = LocalDateTime.now().plusMinutes(expString);
        final var data = Date.from(dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant());

//        final var claims = new HashMap<String, Object>();
//        claims.put("emaildousuario", "usuario@gmail.com");
//        claims.put("roles", "admin");

        return Jwts
                .builder()
                .setSubject(usuario.getLogin())
                .setExpiration(data)
//                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, chaveAssinatura)
                .compact();
    }

    private Claims obterClaims(String token) throws ExpiredJwtException {
        return Jwts
                .parser()
                .setSigningKey(chaveAssinatura)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean tokenValido(String token) {
        try {
            final var claims = obterClaims(token);
            final var dataExpiracao = claims.getExpiration();
            final var expiracaoLocalDate  = dataExpiracao
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            return !LocalDateTime.now().isAfter(expiracaoLocalDate);
        } catch (Exception ex) {
            return false;
        }
    }

    public String obterLoginUsuario(String token) throws ExpiredJwtException {
        return obterClaims(token).getSubject();
    }

}
