package stub.comando.controlador_de_partida;

import model.agentes.IControladorGeralVisaoAplicacaoServidor;
import stub.comando.Parametros;

public class ExibirTelaJogo extends ComandoControladorDePartida {
    
    public ExibirTelaJogo(String codigo, IControladorGeralVisaoAplicacaoServidor controladorPartida) {
        super(codigo, false, controladorPartida);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        super.CONTROLADOR_PARTIDA.exibirTelaJogo();
        return null;
    }
}