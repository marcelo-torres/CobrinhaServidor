package comunicacao;

/**
 * Uma IOException ocorreu duarante a comunicacao.
 */
public class FalhaDeComunicacaoEmTempoRealException extends RuntimeException {
    
    public FalhaDeComunicacaoEmTempoRealException(String mensagem) {
        super(mensagem);
    }
    
    public FalhaDeComunicacaoEmTempoRealException(String mensagem, Throwable e) {
        super(mensagem, e);
    }
}
