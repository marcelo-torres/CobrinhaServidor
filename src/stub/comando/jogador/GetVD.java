package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Parametros;

public class GetVD extends ComandoJogador{
   
    public GetVD(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public Object executar(Parametros parametros) {
        return super.JOGADOR.getVD();
    }
    
}