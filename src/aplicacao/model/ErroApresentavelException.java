package aplicacao.model;

/**
 * Uma exception cujo conteudo textual pode ser exibido para o usuario.
 */
public class ErroApresentavelException extends RuntimeException {
    
    public ErroApresentavelException(String mensagem) {
        super(mensagem);
    }
    
}
