package stub.comando.controlador_de_partida;

import model.agentes.IControladorGeralVisaoAplicacaoServidor;
import stub.comando.Parametros;

public class AdversarioSaiu extends ComandoControladorDePartida {

    public AdversarioSaiu(String codigo, IControladorGeralVisaoAplicacaoServidor controladorPartida) {
        super(codigo, false, controladorPartida);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        super.CONTROLADOR_PARTIDA.adversarioSaiu();
        return null;
    }
}