package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class AndarParaBaixo extends ComandoJogador {
    
    public AndarParaBaixo(String codigo, IJogador jogador) {
        super(codigo, false, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        super.JOGADOR.andarParaBaixo();
        return null;
    }
}