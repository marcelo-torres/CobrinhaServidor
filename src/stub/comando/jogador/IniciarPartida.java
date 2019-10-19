package stub.comando.jogador;

import stub.comando.Parametros;
import model.agentes.IJogadorVisaoStubServidor;

public class IniciarPartida extends ComandoJogador {
    
    public IniciarPartida(String codigo, IJogadorVisaoStubServidor jogador) {
        super(codigo, false, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        super.JOGADOR.iniciarPartida();
        return null;
    }
}