package stub.comando.jogador;

import stub.comando.Parametros;
import model.agentes.IJogadorVisaoStubServidor;

public class GetVD extends ComandoJogador{
   
    public GetVD(String codigo, IJogadorVisaoStubServidor jogador) {
        super(codigo, true, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        return super.JOGADOR.getVD();
    }
    
}