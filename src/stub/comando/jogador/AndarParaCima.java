package stub.comando.jogador;

import aplicacao.model.agentes.Jogador;

public class AndarParaCima extends ComandoJogador {
    
    public AndarParaCima(String codigo, Jogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar() {
        super.JOGADOR.andarParaCima();
    }
    
    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}