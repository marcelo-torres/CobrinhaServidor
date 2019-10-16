package stub.comando.gerenciador_de_udp;

import stub.Stub;
import stub.comando.Parametros;

public class IniciarPedidoDeAberturaUDP extends ComandoGerenciadorDePartida {
    
    public IniciarPedidoDeAberturaUDP(String codigo, Stub.GerenciadorDeConexaoUDPRemota gerenciador) {
        super(codigo, false, gerenciador);
    }

    @Override
    public Object executar(Parametros parametros) {
        super.GERENCIADOR.iniciarPedidoDeAberturaUDP();
        return null;
    }
}