package stub.comando.jogador;

import model.agentes.Jogador;

public class IniciarPartida extends ComandoJogador {
    
    public IniciarPartida(String codigo, Jogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar() {
        super.JOGADOR.iniciarPartida();
    }
    
    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}