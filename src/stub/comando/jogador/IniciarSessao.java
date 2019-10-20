package stub.comando.jogador;

import model.agentes.IJogadorVisaoStubServidor;
import stub.comando.Parametros;

public class IniciarSessao extends ComandoJogador {

    public IniciarSessao(String codigo, IJogadorVisaoStubServidor jogador) {
        super(codigo, false, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        IniciarSessaoParametros iniciarSessaoParametros = new IniciarSessaoParametros();
        super.JOGADOR.iniciarSessao(iniciarSessaoParametros.getNomeJogador());
        return null;
    }
    
}