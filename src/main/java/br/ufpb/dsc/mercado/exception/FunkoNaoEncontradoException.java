package br.ufpb.dsc.mercado.exception;

public class FunkoNaoEncontradoException extends RuntimeException {

    public FunkoNaoEncontradoException(Long id) {
        super("Funko não encontrado com id: " + id);
    }
}
