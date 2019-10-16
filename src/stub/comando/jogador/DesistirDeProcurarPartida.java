package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class DesistirDeProcurarPartida extends ComandoJogador {
    
    public DesistirDeProcurarPartida(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        super.JOGADOR.desistirDeProcurarPartida();
        return null;
    }
}