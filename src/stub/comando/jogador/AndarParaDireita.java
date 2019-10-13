package stub.comando.jogador;

import aplicacao.jogo.Jogador;

public class AndarParaDireita extends ComandoJogador {
    
    public AndarParaDireita(String codigo, Jogador jogador) {
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