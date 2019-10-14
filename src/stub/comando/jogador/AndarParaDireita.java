package stub.comando.jogador;

import model.agentes.IJogador;

public class AndarParaDireita extends ComandoJogador {
    
    public AndarParaDireita(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar() {
        super.JOGADOR.andarParaDireita();
    }
    
    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}