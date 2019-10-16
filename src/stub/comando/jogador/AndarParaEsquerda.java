package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class AndarParaEsquerda extends ComandoJogador {
    
    public AndarParaEsquerda(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        super.JOGADOR.andarParaEsquerda();
        return null;
    }
}