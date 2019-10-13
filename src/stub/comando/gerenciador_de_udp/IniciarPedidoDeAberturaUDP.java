package stub.comando.gerenciador_de_udp;

import stub.Stub;

public class IniciarPedidoDeAberturaUDP extends ComandoGerenciadorDePartida {
    
    public IniciarPedidoDeAberturaUDP(String codigo, Stub.GerenciadorDeConexaoUDPRemota gerenciador) {
        super(codigo, gerenciador);
    }

    @Override
    public void executar() {
        super.GERENCIADOR.iniciarPedidoDeAberturaUDP();
    }
    
    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}