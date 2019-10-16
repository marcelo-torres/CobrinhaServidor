package stub.comando;

public class ComandoExibirMensagem extends Comando {
    
    /**
     * CLASSE DE DEBUG
     * @param codigo 
     */
    
    public ComandoExibirMensagem(String codigo) {
        super(codigo, false);
    }

    private String mensagem;
    
    @Override
    public Object executar(Parametros parametros) {
        ComandoExibirMensagemParametros comandoExibirMensagemParametros = (ComandoExibirMensagemParametros) parametros;
        this.mensagem = comandoExibirMensagemParametros.getMensagem();
        System.out.println("PADRAO COMANDO EXIBE MENSAGEM REMOTA: " + mensagem);
        return null;
    }
}
