package stub.comando.gerenciador_de_udp;

import stub.Stub;
import stub.comando.Parametros;

public class ContinuarAberturaUDP extends ComandoGerenciadorDePartida {
    
    private Integer portaUDPServidor = null;
    
    public ContinuarAberturaUDP(String codigo, Stub.GerenciadorDeConexaoUDPRemota gerenciador) {
        super(codigo, gerenciador);
    }
    
    @Override
    public void executar(Parametros parametros) {
        ContinuarAberturaUDPParametros continuarAberturaUDPParametros = (ContinuarAberturaUDPParametros) parametros;
        super.GERENCIADOR.continuarAberturaUDP(continuarAberturaUDPParametros.getPortaUDPServidor());
    }
}