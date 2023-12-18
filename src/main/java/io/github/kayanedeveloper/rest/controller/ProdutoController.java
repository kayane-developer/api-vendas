package io.github.kayanedeveloper.rest.controller;

import io.github.kayanedeveloper.domain.entity.Produto;
import io.github.kayanedeveloper.domain.repository.ProdutoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/produtos")
@Api("Api Produto")
public class ProdutoController {

    private final ProdutoRepository repository;

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation("Salva um novo produto")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Produto salvo com sucesso"),
            @ApiResponse(code = 404, message = "Erro de validação")
    })
    public Produto save( @RequestBody @Valid Produto produto ){
        return repository.save(produto);
    }

    @PutMapping("{id}")
    @ApiOperation("Atualiza os dados do produto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Produto atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Produto não encontrado")
    })
    public Produto update(@PathVariable Integer id, @RequestBody @Valid Produto produtoAtualizar ) {
        return repository
                .findById(id)
                .map( produto -> {
                    produtoAtualizar.setId(produto.getId());
                    repository.save(produtoAtualizar);
                    return produtoAtualizar;
                }).orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado."));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Deleta o produto a partir do id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Produto deletado"),
            @ApiResponse(code = 404, message = "Produto não encontrado")
    })
    public void delete(@PathVariable Integer id) {
        repository
                .findById(id)
                .map( produto -> {
                    repository.delete(produto);
                    return Void.TYPE;
                }).orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado."));
    }

    @GetMapping("{id}")
    @ApiOperation("Obter detalhes de um produto por id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Produto encontrado com sucesso"),
            @ApiResponse(code = 404, message = "Produto não encontrado para o id informado")
    })
    public Produto getById(@PathVariable Integer id) {
        return repository
                .findById(id)
                .orElseThrow( () ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Produto não encontrado."));
    }

    @GetMapping
    @ApiOperation("Obter detalhes de um produto a partir dos dados")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Produto encontrado"),
            @ApiResponse(code = 404, message = "Produto não encontrado para os dados informados")
    })
    public List<Produto> find(Produto filtro ) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING );

        final var example = Example.of(filtro, matcher);
        return repository.findAll(example);
    }

}
