package stub.comando.jogador;

import aplicacao.jogo.Jogador;

public class AndarParaBaixo extends ComandoJogador {
    
    public AndarParaBaixo(String codigo, Jogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar() {
        super.JOGADOR.andarParaBaixo();
    }

    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}