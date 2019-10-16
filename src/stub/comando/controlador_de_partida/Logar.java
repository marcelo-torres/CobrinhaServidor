package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;
import stub.comando.Parametros;

public class Logar extends ComandoControladorDePartida {
    
    public Logar(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, false, controladorPartida);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        LogarParametros logarParametros = (LogarParametros) parametros;
        super.CONTROLADOR_PARTIDA.logar(logarParametros.getLogin());
        return null;
    }
}