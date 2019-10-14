package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;

public class VocePerdeu extends ComandoControladorDePartida {
    
    public VocePerdeu(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, controladorPartida);
    }
    
    @Override
    public void executar() {
        super.CONTROLADOR_PARTIDA.vocerPerdeu();
    }
 
    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}
