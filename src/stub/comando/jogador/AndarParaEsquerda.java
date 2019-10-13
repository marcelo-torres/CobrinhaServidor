package stub.comando.jogador;

import aplicacao.model.agentes.Jogador;

public class AndarParaEsquerda extends ComandoJogador {
    
    public AndarParaEsquerda(String codigo, Jogador jogador) {
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