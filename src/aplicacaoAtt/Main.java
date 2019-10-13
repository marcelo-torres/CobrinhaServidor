
import controller.ControladorGeral;
import controller.ControladorDePartida;
import model.Jogador;

public class Main {
    
    public static void main(String[] args) {
        Jogador jg1 = new Jogador();
        Jogador jg2 = new Jogador();
        
        ControladorGeral controladorGeral = new ControladorGeral();
        
        ControladorDePartida controladorDePartida = new ControladorDePartida(jg1, jg2, controladorGeral);
    }
}
