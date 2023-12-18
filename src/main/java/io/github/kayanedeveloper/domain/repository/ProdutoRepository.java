package io.github.kayanedeveloper.domain.repository;

import io.github.kayanedeveloper.domain.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository  extends JpaRepository<Produto,Integer> {

}
