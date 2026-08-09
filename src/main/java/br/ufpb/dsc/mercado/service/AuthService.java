package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.domain.Role;
import br.ufpb.dsc.mercado.domain.Usuario;
import br.ufpb.dsc.mercado.dto.LoginRequest;
import br.ufpb.dsc.mercado.dto.RegisterRequest;
import br.ufpb.dsc.mercado.dto.TokenResponse;
import br.ufpb.dsc.mercado.exception.EmailJaCadastradoException;
import br.ufpb.dsc.mercado.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository repository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenResponse registrar(RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }
        Usuario usuario = new Usuario(
                request.email(),
                passwordEncoder.encode(request.senha()),
                Role.CUSTOMER
        );
        repository.save(usuario);
        return TokenResponse.bearer(
                jwtService.gerarToken(usuario),
                usuario.getEmail(),
                usuario.getRole().name()
        );
    }

    @Transactional(readOnly = true)
    public TokenResponse autenticar(LoginRequest request) {
        Usuario usuario = repository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));
        if (!passwordEncoder.matches(request.senha(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
        return TokenResponse.bearer(
                jwtService.gerarToken(usuario),
                usuario.getEmail(),
                usuario.getRole().name()
        );
    }
}
