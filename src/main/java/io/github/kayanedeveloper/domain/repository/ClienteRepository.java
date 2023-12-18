package io.github.kayanedeveloper.domain.repository;

import io.github.kayanedeveloper.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer > {

}
