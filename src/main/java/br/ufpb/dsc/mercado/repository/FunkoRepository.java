package br.ufpb.dsc.mercado.repository;

import br.ufpb.dsc.mercado.domain.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FunkoRepository extends JpaRepository<Funko, Long> {

    Page<Funko> findByNomeContainingIgnoreCaseOrFranquiaContainingIgnoreCase(
            String nome, String franquia, Pageable pageable);
}
