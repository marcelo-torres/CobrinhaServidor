package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class GetLocalAtual extends ComandoJogador {

    public GetLocalAtual(String codigo, IJogador jogador) {
        super(codigo, true, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        return super.JOGADOR.getLocalAtual();
    }
}