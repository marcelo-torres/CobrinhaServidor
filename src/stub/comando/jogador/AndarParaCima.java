package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class AndarParaCima extends ComandoJogador {
    
    public AndarParaCima(String codigo, IJogador jogador) {
        super(codigo, false, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        super.JOGADOR.andarParaCima();
        return null;
    }
}