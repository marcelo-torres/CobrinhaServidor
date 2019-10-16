package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;
import stub.comando.Parametros;

public class EntregarQuadroComando extends ComandoControladorDePartida {

    public EntregarQuadroComando(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, controladorPartida);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        EntregarQuadroParametro entregarQuadroParametro = (EntregarQuadroParametro) parametros;
        this.CONTROLADOR_PARTIDA.entregarQuadro(entregarQuadroParametro.getArena());
        return null;
    }
    
    
    
}
