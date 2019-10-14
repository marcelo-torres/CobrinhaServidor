package stub.comando.jogador;

import model.agentes.IJogador;

public class AndarParaEsquerda extends ComandoJogador {
    
    public AndarParaEsquerda(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar() {
        super.JOGADOR.andarParaEsquerda();
    }
    
    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}