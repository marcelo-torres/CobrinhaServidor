package stub.comando.gerenciador_de_udp;

import stub.comando.Parametros;

public class AtenderPedidoInicioDeAberturaUDPParametros extends Parametros {
    
    private int portaUDPServidor;
    
    public void setPortaUDPServidor(int portaUDPServidor) {
        this.portaUDPServidor = portaUDPServidor;
    }
    
    public int getPortaUDPServidor() {
        return this.portaUDPServidor;
    }
    
}
