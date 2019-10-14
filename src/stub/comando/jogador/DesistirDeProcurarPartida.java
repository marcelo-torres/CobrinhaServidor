package stub.comando.jogador;

import model.agentes.IJogador;

public class DesistirDeProcurarPartida extends ComandoJogador {
    
    public DesistirDeProcurarPartida(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar() {
        super.JOGADOR.desistirDeProcurarPartida();
    }
    
    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}