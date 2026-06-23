package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.domain.LogAuditoria;
import br.ufpb.dsc.mercado.repository.LogAuditoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    private final LogAuditoriaRepository repository;

    public AuditoriaService(LogAuditoriaRepository repository) {
        this.repository = repository;
    }

    // REQUIRES_NEW garante que o log é persistido mesmo se a transação principal sofrer rollback
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String recurso, String recursoId, String operacao,
                          String usuario, String detalhe, String status, String mensagemErro) {
        LogAuditoria entrada = new LogAuditoria(
                recurso, recursoId, operacao, usuario, detalhe, status, mensagemErro);
        repository.save(entrada);
        log.info("[AUDITORIA] {} {} id={} usuario={} status={}",
                operacao, recurso, recursoId, usuario, status);
    }
}
