package stub.comando.gerenciador_de_udp;

import stub.Stub;
import stub.comando.Parametros;

public class AtenderPedidoInicioDeAberturaUDP extends ComandoGerenciadorDePartida {
    
    public AtenderPedidoInicioDeAberturaUDP(String codigo, Stub.GerenciadorDeConexaoUDPRemota gerenciador) {
        super(codigo, gerenciador);
    }
    
    @Override
    public void executar(Parametros parametros) {
        AtenderPedidoInicioDeAberturaUDPParametros atenderPedidoInicioDeAberturaUDPParametros = (AtenderPedidoInicioDeAberturaUDPParametros) parametros; 
        super.GERENCIADOR.atenderPedidoInicioDeAberturaUDP(atenderPedidoInicioDeAberturaUDPParametros.getPortaUDPServidor());
    }
}