package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class AndarParaDireita extends ComandoJogador {
    
    public AndarParaDireita(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        super.JOGADOR.andarParaDireita();
        return null;
    }
}