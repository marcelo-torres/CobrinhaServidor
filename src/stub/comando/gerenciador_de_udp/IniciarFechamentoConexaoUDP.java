package stub.comando.gerenciador_de_udp;

import stub.Stub;

public class IniciarFechamentoConexaoUDP extends ComandoGerenciadorDePartida {

    public IniciarFechamentoConexaoUDP(String codigo, Stub.GerenciadorDeConexaoUDPRemota gerenciador) {
        super(codigo, gerenciador);
    }
    
    @Override
    public void executar() {
        super.GERENCIADOR.iniciarFechamentoConexaoUDP();
    }

    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}
