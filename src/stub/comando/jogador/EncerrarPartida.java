package stub.comando.jogador;

import model.agentes.IJogador;

public class EncerrarPartida extends ComandoJogador {
    
    public EncerrarPartida(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar() {
        super.JOGADOR.encerrarPartida();
    }
    
    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}