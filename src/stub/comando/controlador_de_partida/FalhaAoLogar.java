package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;
import stub.comando.Parametros;

public class FalhaAoLogar extends ComandoControladorDePartida {
    
    public FalhaAoLogar(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, false, controladorPartida);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        FalhaAoLogarParametros falhaAoLogarParametros = (FalhaAoLogarParametros) parametros; 
        super.CONTROLADOR_PARTIDA.logar(falhaAoLogarParametros.getMensagem());
        return null;
    }
}