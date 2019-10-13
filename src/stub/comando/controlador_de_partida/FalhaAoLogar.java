package stub.comando.controlador_de_partida;

import aplicacao.model.agentes.ControladorDePartida;

public class FalhaAoLogar extends ComandoControladorDePartida {
    
    public FalhaAoLogar(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, controladorPartida);
    }
    
    private String mensagem = null;
    
    @Override
    public void executar() {
        if(this.mensagem == null) {
            throw new RuntimeException("Nao eh possivel executar o comando: mensagem nao definida");
        }
        super.CONTROLADOR_PARTIDA.logar(this.mensagem);
    }
    
    @Override
    public void definirParametros(String... parametros) {
       this.mensagem = parametros[0];
    }
}