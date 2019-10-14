package stub.comando.jogador;

import model.agentes.IJogador;

public class AndarParaCima extends ComandoJogador {
    
    public AndarParaCima(String codigo, IJogador jogador) {
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