package stub.comando.controlador_de_partida;

import aplicacao.jogo.ControladorDePartida;

public class AdversarioSaiu extends ComandoControladorDePartida {

    public AdversarioSaiu(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, controladorPartida);
    }
    
    @Override
    public void executar() {
        super.CONTROLADOR_PARTIDA.adversarioSaiu();
    } 
 
    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}