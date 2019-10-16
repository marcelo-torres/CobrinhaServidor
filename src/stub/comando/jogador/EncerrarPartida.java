package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class EncerrarPartida extends ComandoJogador {
    
    public EncerrarPartida(String codigo, IJogador jogador) {
        super(codigo, false, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        super.JOGADOR.encerrarPartida();
        return null;
    }
}