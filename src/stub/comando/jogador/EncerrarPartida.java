package stub.comando.jogador;

import model.agentes.Jogador;

public class EncerrarPartida extends ComandoJogador {
    
    public EncerrarPartida(String codigo, Jogador jogador) {
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