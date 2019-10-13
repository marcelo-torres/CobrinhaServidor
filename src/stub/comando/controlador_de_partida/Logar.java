package stub.comando.controlador_de_partida;

import aplicacao.model.agentes.ControladorDePartida;

public class Logar extends ComandoControladorDePartida {
    
    public Logar(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, controladorPartida);
    }
    
    private String login = null;
    
    @Override
    public void executar() {
        if(this.login == null) {
            throw new RuntimeException("Nao eh possivel executar o comando: login nao definido");
        }
        super.CONTROLADOR_PARTIDA.logar(this.login);
    }
    
    @Override
    public void definirParametros(String... parametros) {
       this.login = parametros[0];
    }
}