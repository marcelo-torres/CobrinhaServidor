package stub.comando.controlador_de_partida;

import model.send.Arena;
import stub.comando.Parametros;

public class EntregarQuadroParametro extends Parametros {
    
    private Arena arena;
    
    public void setArena(Arena arena) {
        this.arena = arena;
    }
    
    public Arena getArena() {
        return this.arena;
    }
    
}