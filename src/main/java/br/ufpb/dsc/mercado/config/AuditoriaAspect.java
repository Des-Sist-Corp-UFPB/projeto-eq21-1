package br.ufpb.dsc.mercado.config;

import br.ufpb.dsc.mercado.domain.Funko;
import br.ufpb.dsc.mercado.dto.FunkoRequest;
import br.ufpb.dsc.mercado.dto.LoginRequest;
import br.ufpb.dsc.mercado.dto.RegisterRequest;
import br.ufpb.dsc.mercado.service.AuditoriaService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AuditoriaAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaAspect.class);

    private final AuditoriaService auditoriaService;

    public AuditoriaAspect(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @Around("execution(* br.ufpb.dsc.mercado.service.FunkoService.criar(..)) || " +
            "execution(* br.ufpb.dsc.mercado.service.FunkoService.atualizar(..)) || " +
            "execution(* br.ufpb.dsc.mercado.service.FunkoService.excluir(..))")
    public Object auditarFunko(ProceedingJoinPoint pjp) throws Throwable {
        String operacao = resolverOperacao(pjp.getSignature().getName());
        String usuario = resolverUsuarioAtual();
        String detalhe = "nome=" + resolverNomeFunko(pjp);

        try {
            Object resultado = pjp.proceed();
            String recursoId = resolverRecursoIdFunko(pjp, resultado);
            salvarLog("FUNKO", recursoId, operacao, usuario, detalhe, "SUCESSO", null);
            return resultado;
        } catch (Throwable t) {
            salvarLog("FUNKO", resolverIdArg(pjp), operacao, usuario, detalhe, "FALHA", t.getMessage());
            throw t;
        }
    }

    @Around("execution(* br.ufpb.dsc.mercado.service.AuthService.registrar(..)) || " +
            "execution(* br.ufpb.dsc.mercado.service.AuthService.autenticar(..))")
    public Object auditarAuth(ProceedingJoinPoint pjp) throws Throwable {
        String operacao = "registrar".equals(pjp.getSignature().getName()) ? "REGISTRAR" : "LOGIN";
        String email = resolverEmailDosArgs(pjp.getArgs());

        try {
            Object resultado = pjp.proceed();
            salvarLog("USUARIO", null, operacao, email, "email=" + email, "SUCESSO", null);
            return resultado;
        } catch (Throwable t) {
            salvarLog("USUARIO", null, operacao, email, "email=" + email, "FALHA", t.getMessage());
            throw t;
        }
    }

    private void salvarLog(String recurso, String recursoId, String operacao,
                           String usuario, String detalhe, String status, String erro) {
        try {
            auditoriaService.registrar(recurso, recursoId, operacao, usuario, detalhe, status, erro);
        } catch (Exception e) {
            log.error("Falha ao persistir log de auditoria: recurso={} operacao={}", recurso, operacao, e);
        }
    }

    private String resolverUsuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "anonimo";
    }

    private String resolverOperacao(String nomeMetodo) {
        return switch (nomeMetodo) {
            case "criar" -> "CRIAR";
            case "atualizar" -> "ATUALIZAR";
            case "excluir" -> "EXCLUIR";
            default -> nomeMetodo.toUpperCase();
        };
    }

    private String resolverRecursoIdFunko(ProceedingJoinPoint pjp, Object resultado) {
        if ("criar".equals(pjp.getSignature().getName()) && resultado instanceof Funko f) {
            return f.getId() != null ? String.valueOf(f.getId()) : null;
        }
        return resolverIdArg(pjp);
    }

    private String resolverIdArg(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        if (args.length > 0 && args[0] instanceof Long id) {
            return String.valueOf(id);
        }
        return null;
    }

    private String resolverNomeFunko(ProceedingJoinPoint pjp) {
        for (Object arg : pjp.getArgs()) {
            if (arg instanceof FunkoRequest req) {
                return req.nome();
            }
        }
        return "desconhecido";
    }

    private String resolverEmailDosArgs(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof LoginRequest req) return req.email();
            if (arg instanceof RegisterRequest req) return req.email();
        }
        return "desconhecido";
    }
}
