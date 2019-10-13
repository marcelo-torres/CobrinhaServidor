package stub.comando.gerenciador_de_udp;

import stub.Stub.GerenciadorDeConexaoUDPRemota;
import stub.comando.Comando;

public abstract class ComandoGerenciadorDePartida extends Comando {
    
    protected final GerenciadorDeConexaoUDPRemota GERENCIADOR;
    
    public ComandoGerenciadorDePartida(String codigo, GerenciadorDeConexaoUDPRemota gerenciador) {
        super(codigo);
        
        this.GERENCIADOR = gerenciador;
    }
    
}
