package stub.comando.jogador;

import stub.comando.Parametros;
import localizacoes.ILocal;

public class SetLocalAtualParametros extends Parametros {
    
    private ILocal local;
    
    public void setLocal(ILocal local) {
        this.local = local;
    }
    
    public ILocal getLocal() {
        return this.local;
    }
}
