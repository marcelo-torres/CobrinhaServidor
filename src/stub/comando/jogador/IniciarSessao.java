package stub.comando.jogador;

import model.agentes.IJogadorVisaoStubServidor;
import stub.comando.Parametros;

public class IniciarSessao extends ComandoJogador {

    public IniciarSessao(String codigo, IJogadorVisaoStubServidor jogador) {
        super(codigo, false, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        IniciarSessaoParametros iniciarSessaoParametros = (IniciarSessaoParametros) parametros;
        super.JOGADOR.iniciarSessao(iniciarSessaoParametros.getNomeJogador());
        return null;
    }
    
}