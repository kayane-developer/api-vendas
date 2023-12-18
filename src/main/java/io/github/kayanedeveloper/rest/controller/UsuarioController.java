package io.github.kayanedeveloper.rest.controller;

import io.github.kayanedeveloper.domain.entity.Usuario;
import io.github.kayanedeveloper.exception.SenhaInvalidaException;
import io.github.kayanedeveloper.rest.dto.CredenciaisDTO;
import io.github.kayanedeveloper.rest.dto.TokenDTO;
import io.github.kayanedeveloper.security.jwt.JwtService;
import io.github.kayanedeveloper.service.impl.UsuarioServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("api/usuarios")
@RequiredArgsConstructor
@Api("Api Usuario")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salva um novo usuário")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Usuário salvo com sucesso"),
            @ApiResponse(code = 404, message = "Erro de validação")
    })
    public Usuario salvar(@RequestBody @Valid Usuario usuario) {
        final var senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        return usuarioService.salvar(usuario);
    }

    @PostMapping("/auth")
    @ApiOperation("Autenticar credenciais do usuário")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuário autenticado com sucesso"),
            @ApiResponse(code = 401, message = "Usuário não autorizado")
    })
    public TokenDTO autenticar(@RequestBody CredenciaisDTO credenciaisDTO) {
        try {
            final var usuario = Usuario
                    .builder()
                    .login(credenciaisDTO.getLogin())
                    .senha(credenciaisDTO.getSenha())
                    .build();
            usuarioService.autenticar(usuario);
            final var token = jwtService.gerarToken(usuario);
            return new TokenDTO(usuario.getLogin(), token);
        } catch (UsernameNotFoundException | SenhaInvalidaException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

}
