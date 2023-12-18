package io.github.kayanedeveloper.service;

import io.github.kayanedeveloper.domain.entity.Pedido;
import io.github.kayanedeveloper.domain.enums.StatusPedidoEnum;
import io.github.kayanedeveloper.rest.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoService {

    Pedido salvar( PedidoDTO dto );
    Optional<Pedido> obterPedidoCompleto(Integer id);
    void atualizaStatus(Integer id, StatusPedidoEnum statusPedido);

}
