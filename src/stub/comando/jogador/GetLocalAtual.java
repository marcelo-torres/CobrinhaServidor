package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class GetLocalAtual extends ComandoJogador {

    public GetLocalAtual(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar(Parametros parametros) {
        super.JOGADOR.getLocalAtual();
    }
}