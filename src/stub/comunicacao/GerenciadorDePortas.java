package stub.comunicacao;

import java.util.HashMap;
import java.util.LinkedList;

public class GerenciadorDePortas {

    private LinkedList<Integer> PORTAS_DISPONIVEIS = new LinkedList<>();
    private HashMap<Integer, Boolean> PORTAS_USADAS = new HashMap<>();

    private final int INICIO_INTERVALO;
    private final int FIM_INTERVALO;

    private final boolean ALEATORIO;
    private final Object LOCKER_FILA = new Object();

    public GerenciadorDePortas() {
        this.ALEATORIO = true;
        
        this.INICIO_INTERVALO = -1;
        this.FIM_INTERVALO = -1;
    }
    
    public GerenciadorDePortas(int inicioIntervalo, int fimIntervalo) {
        this.ALEATORIO = (fimIntervalo - inicioIntervalo < 0);

        this.INICIO_INTERVALO = inicioIntervalo;
        this.FIM_INTERVALO = fimIntervalo;

        if(!this.ALEATORIO) {
            for(int porta = this.INICIO_INTERVALO; porta <= this.FIM_INTERVALO; porta++) {
                this.PORTAS_DISPONIVEIS.add(porta);
                this.PORTAS_USADAS.put(porta, false);
            }
        }
    }

    public int getPorta() {
        synchronized (this.LOCKER_FILA) {
            if(this.ALEATORIO) {
                return -1; //codigo pro comunicador saber que eh aleatoria
            }
            if(this.PORTAS_DISPONIVEIS.size() == 0) {
                throw new RuntimeException("Nenhuma porta disponivel");
            }

            int porta = PORTAS_DISPONIVEIS.removeFirst();
            this.PORTAS_USADAS.put(porta, true);

            return porta;
        }
    }

    public void liberarPorta(int porta) {
        synchronized (this.LOCKER_FILA) {
            Boolean usada = this.PORTAS_USADAS.get(porta);
            if(usada != null && usada) {
                this.PORTAS_DISPONIVEIS.add(porta);
                this.PORTAS_USADAS.put(porta, false);
            }
        }
    }
}