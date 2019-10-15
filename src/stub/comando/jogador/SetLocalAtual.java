package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class SetLocalAtual extends ComandoJogador {
 
    public SetLocalAtual(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar(Parametros parametros) {
        SetLocalAtualParametros setLocalAtualParametros = (SetLocalAtualParametros) parametros;
        super.JOGADOR.setLocalAtual(setLocalAtualParametros.getLocal());
    }
}