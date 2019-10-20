package stub.comando.jogador;

import stub.comando.Parametros;
import model.agentes.IJogadorVisaoStubServidor;

public class EncerrarPartida extends ComandoJogador {
    
    public EncerrarPartida(String codigo, IJogadorVisaoStubServidor jogador) {
        super(codigo, true, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        return super.JOGADOR.encerrarPartida();
    }
}