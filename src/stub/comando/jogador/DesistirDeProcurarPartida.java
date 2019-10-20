package stub.comando.jogador;

import stub.comando.Parametros;
import model.agentes.IJogadorVisaoStubServidor;

public class DesistirDeProcurarPartida extends ComandoJogador {
    
    public DesistirDeProcurarPartida(String codigo, IJogadorVisaoStubServidor jogador) {
        super(codigo, true, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        return super.JOGADOR.desistirDeProcurarPartida();
    }
}