package stub.comando;

public class ComandoExibirMensagem extends Comando {
    
    public ComandoExibirMensagem(String codigo) {
        super(codigo);
    }

    private String mensagem;
    
    @Override
    public void executar() {
        System.out.println("PADRAO COMANDO EXIBE MENSAGEM REMOTA: " + mensagem);
    }

    @Override
    public void definirParametros(String... parametros) {
        this.mensagem = parametros[0];
    }
    
}
