package stub.comando.controlador_de_partida;

import model.agentes.IControladorGeralVisaoAplicacaoServidor;
import stub.comando.Comando;

public abstract class ComandoControladorDePartida extends Comando {
    
    protected final IControladorGeralVisaoAplicacaoServidor CONTROLADOR_PARTIDA;
    
    public ComandoControladorDePartida(String codigo, boolean possuiRetorno, IControladorGeralVisaoAplicacaoServidor controladorPartida) {
        super(codigo, possuiRetorno);
        this.CONTROLADOR_PARTIDA = controladorPartida;
    }
    
}