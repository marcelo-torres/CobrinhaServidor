package stub.comando.gerenciador_de_udp;

import stub.Stub;
import stub.comando.Parametros;

public class ContinuarAberturaUDP extends ComandoGerenciadorDePartida {
    
    public ContinuarAberturaUDP(String codigo, Stub.GerenciadorDeConexaoUDPRemota gerenciador) {
        super(codigo, false, gerenciador);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        ContinuarAberturaUDPParametros continuarAberturaUDPParametros = (ContinuarAberturaUDPParametros) parametros;
        super.GERENCIADOR.continuarAberturaUDP(continuarAberturaUDPParametros.getPortaUDPServidor());
        return null;
    }
}