package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class IniciarPartida extends ComandoJogador {
    
    public IniciarPartida(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        super.JOGADOR.iniciarPartida();
        return null;
    }
}