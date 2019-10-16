package stub.comando.gerenciador_de_udp;

import stub.Stub;
import stub.comando.Parametros;


public class FecharConexaoUDP extends ComandoGerenciadorDePartida {

    public FecharConexaoUDP(String codigo, Stub.GerenciadorDeConexaoUDPRemota gerenciador) {
        super(codigo, gerenciador);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        super.GERENCIADOR.fecharConexaoUDP();
        return null;
    }
}
