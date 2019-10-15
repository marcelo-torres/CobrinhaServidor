package stub.comando;

public class ComandoExibirMensagem extends Comando {
    
    public ComandoExibirMensagem(String codigo) {
        super(codigo);
    }

    private String mensagem;
    
    @Override
    public void executar(Parametros parametros) {
        ComandoExibirMensagemParametros comandoExibirMensagemParametros = (ComandoExibirMensagemParametros) parametros;
        this.mensagem = comandoExibirMensagemParametros.getMensagem();
        System.out.println("PADRAO COMANDO EXIBE MENSAGEM REMOTA: " + mensagem);
    }
}
