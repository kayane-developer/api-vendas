package io.github.kayanedeveloper.service.impl;

import io.github.kayanedeveloper.domain.entity.Cliente;
import io.github.kayanedeveloper.domain.entity.ItemPedido;
import io.github.kayanedeveloper.domain.entity.Pedido;
import io.github.kayanedeveloper.domain.entity.Produto;
import io.github.kayanedeveloper.domain.enums.StatusPedidoEnum;
import io.github.kayanedeveloper.domain.repository.ClienteRepository;
import io.github.kayanedeveloper.domain.repository.ItemPedidoRepository;
import io.github.kayanedeveloper.domain.repository.PedidoRepository;
import io.github.kayanedeveloper.domain.repository.ProdutoRepository;
import io.github.kayanedeveloper.exception.PedidoNaoEncontradoException;
import io.github.kayanedeveloper.exception.RegraNegocioException;
import io.github.kayanedeveloper.rest.dto.ItemPedidoDTO;
import io.github.kayanedeveloper.rest.dto.PedidoDTO;
import io.github.kayanedeveloper.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository repository;
    private final ClienteRepository clientesRepository;
    private final ProdutoRepository produtosRepository;
    private final ItemPedidoRepository itemsPedidoRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto ) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);

        List<ItemPedido> itemsPedido = converterItems(pedido, dto.getItems());
        repository.save(pedido);
        itemsPedidoRepository.saveAll(itemsPedido);
        pedido.setItens(itemsPedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizaStatus( Integer id, StatusPedidoEnum statusPedido ) {
        repository
                .findById(id)
                .ifPresentOrElse(pedido -> {
                    pedido.setStatus(statusPedido);
                    repository.save(pedido);
                }, () -> {
                    throw new PedidoNaoEncontradoException();
                });
    }

    private List<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items){
        if(items.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um pedido sem items.");
        }

        return items
                .stream()
                .map( dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(
                                    () -> new RegraNegocioException(
                                            "Código de produto inválido: "+ idProduto
                                    ));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());

    }

}
