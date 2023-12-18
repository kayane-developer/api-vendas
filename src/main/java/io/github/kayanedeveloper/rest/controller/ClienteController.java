package io.github.kayanedeveloper.rest.controller;

import io.github.kayanedeveloper.domain.entity.Cliente;
import io.github.kayanedeveloper.domain.repository.ClienteRepository;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/clientes")
@Api("Api Clientes")
public class ClienteController {

    private static final String MSG_CLIENTE_NAO_ENCONTRADO = "Cliente não encontrado";

    private final ClienteRepository repository;

    @GetMapping("{id}")
    @ApiOperation("Obter detalhes de um cliente por id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente encontrado"),
            @ApiResponse(code = 404, message = "Cliente não encontrado para o id informado")
    })
    public Cliente getClienteById(@PathVariable @ApiParam("Id do cliente") Integer id ){
        return repository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_CLIENTE_NAO_ENCONTRADO));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salva um novo cliente")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cliente salvo com sucesso"),
            @ApiResponse(code = 404, message = "Erro de validação")
    })
    public Cliente save( @RequestBody @Valid Cliente cliente ){
        return repository.save(cliente);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deleta o cliente a partir do id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Cliente deletado"),
            @ApiResponse(code = 404, message = "Cliente não encontrado")
    })
    public void delete( @PathVariable Integer id ){
        repository.findById(id)
                .ifPresentOrElse(repository::delete, () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_CLIENTE_NAO_ENCONTRADO);
                });

    }

    @PutMapping("{id}")
    @ApiOperation("Atualiza os dados do cliente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente atualizado"),
            @ApiResponse(code = 404, message = "Cliente não encontrado")
    })
    public Cliente update(@PathVariable Integer id,
                          @RequestBody @Valid Cliente cliente ){
        return repository
                .findById(id)
                .map( clienteExistente -> {
                    cliente.setId(clienteExistente.getId());
                    repository.save(cliente);
                    return clienteExistente;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_CLIENTE_NAO_ENCONTRADO) );
    }

    @GetMapping
    @ApiOperation("Obter detalhes de um cliente a partir dos dados")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente encontrado"),
            @ApiResponse(code = 404, message = "Cliente não encontrado para os dados informados")
    })
    public List<Cliente> find(Cliente filtro){
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING );

        final var example = Example.of(filtro, matcher);
        return repository.findAll(example);
    }

}
