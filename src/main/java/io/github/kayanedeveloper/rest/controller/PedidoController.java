package io.github.kayanedeveloper.rest.controller;

import io.github.kayanedeveloper.domain.entity.ItemPedido;
import io.github.kayanedeveloper.domain.entity.Pedido;
import io.github.kayanedeveloper.domain.enums.StatusPedidoEnum;
import io.github.kayanedeveloper.rest.dto.AtualizacaoStatusPedidoDTO;
import io.github.kayanedeveloper.rest.dto.InformacaoItemPedidoDTO;
import io.github.kayanedeveloper.rest.dto.InformacoesPedidoDTO;
import io.github.kayanedeveloper.rest.dto.PedidoDTO;
import io.github.kayanedeveloper.service.PedidoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pedidos")
@Api("Api Pedidos")
public class PedidoController {

    private final PedidoService service;

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation("Salva um novo pedido")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Pedido salvo com sucesso"),
            @ApiResponse(code = 404, message = "Erro de validação")
    })
    public Integer save( @RequestBody @Valid PedidoDTO dto ) {
        Pedido pedido = service.salvar(dto);
        return pedido.getId();
    }

    @GetMapping("{id}")
    @ApiOperation("Obter um pedido por id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pedido encontrado"),
            @ApiResponse(code = 404, message = "Pedido não encontrado para o id informado")
    })
    public InformacoesPedidoDTO getById(@PathVariable Integer id ) {
        return service
                .obterPedidoCompleto(id)
                .map(this::converter)
                .orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND, "Pedido não encontrado."));
    }

    @PatchMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Atualiza o status do pedido")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Status atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Pedido não encontrado")
    })
    public void updateStatus(@PathVariable Integer id ,
                             @RequestBody AtualizacaoStatusPedidoDTO dto) {
        String novoStatus = dto.getNovoStatus();
        service.atualizaStatus(id, StatusPedidoEnum.valueOf(novoStatus));
    }

    private InformacoesPedidoDTO converter(Pedido pedido) {
        return InformacoesPedidoDTO
                .builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .status(pedido.getStatus().name())
                .items(converter(pedido.getItens()))
                .build();
    }

    private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itens) {
        if(CollectionUtils.isEmpty(itens)){
            return Collections.emptyList();
        }
        return itens.stream().map(
                item -> InformacaoItemPedidoDTO
                        .builder().descricaoProduto(item.getProduto().getDescricao())
                        .precoUnitario(item.getProduto().getPreco())
                        .quantidade(item.getQuantidade())
                        .build()
        ).collect(Collectors.toList());
    }

}
