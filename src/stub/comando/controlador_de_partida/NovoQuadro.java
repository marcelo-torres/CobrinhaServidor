package stub.comando.controlador_de_partida;

import model.agentes.IControladorGeralVisaoAplicacaoServidor;
import stub.comando.Parametros;

public class NovoQuadro extends ComandoControladorDePartida {

    public NovoQuadro(String codigo, IControladorGeralVisaoAplicacaoServidor controladorPartida) {
        super(codigo, false, controladorPartida);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        NovoQuadroParametro entregarQuadroParametro = (NovoQuadroParametro) parametros;
        this.CONTROLADOR_PARTIDA.novoQuadro(entregarQuadroParametro.getArena());
        return null;
    }
    
    
    
}
