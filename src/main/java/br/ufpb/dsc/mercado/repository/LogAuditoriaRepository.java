package br.ufpb.dsc.mercado.repository;

import br.ufpb.dsc.mercado.domain.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    List<LogAuditoria> findByRecursoAndRecursoId(String recurso, String recursoId);

    List<LogAuditoria> findByUsuario(String usuario);

    List<LogAuditoria> findByOperacao(String operacao);
}
