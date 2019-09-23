package comunicacao;

import java.util.LinkedList;
import java.util.concurrent.*;

public class FilaMonitorada<T>{
    
    public final T FIM;
    
    private final LinkedList<T> FILA;
    private final int TAMANHO_MAXIMO;

    private final Semaphore SEMAFORO_GERAL;
    private final Semaphore SEMAFORO_CONTADOR;
    
    private boolean fechada = false;
    
    // TODO implementar tamanho 
    
    public FilaMonitorada(int tamanhoMaximo) {
        this.FIM = (T)new Object();
        this.FILA = new LinkedList();
        this.TAMANHO_MAXIMO = tamanhoMaximo;
        this.SEMAFORO_GERAL = new Semaphore(1);
        this.SEMAFORO_CONTADOR = new Semaphore(0);
    }
    
    public void fechar() {
        if(this.fechada) return;
        this.fechada = true;
        
        try {
            //System.out.println("[inserir 1] tentando acquire com o semáforo: " + semaforoGeral);
            SEMAFORO_GERAL.acquire();
            FILA.add(null);
            SEMAFORO_GERAL.release();
            SEMAFORO_CONTADOR.release();
            
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }
    
    public boolean adicionar(T item) {
        if(this.fechada) return false;
        
        try {
            //System.out.println("[inserir 1] tentando acquire com o semáforo: " + semaforoGeral);
            SEMAFORO_GERAL.acquire();
            //System.out.println("[inserir 2] acquire obtido e o semáforo: " + semaforoGeral);
            FILA.add(item);
            SEMAFORO_GERAL.release();
            //System.out.println("[inserir 3] semáforo geral released: " + semaforoGeral);
            SEMAFORO_CONTADOR.release();
            
            return true;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        
        return false;
    }

    public boolean colocarNoInicio(T item) {
        if(this.fechada) return false;
        
        try {
            SEMAFORO_GERAL.acquire();
            FILA.addFirst(item);
            SEMAFORO_GERAL.release();
            SEMAFORO_CONTADOR.release();

            return true;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        
        return false;
    }

    public T remover() {
        if(this.fechada) return null;
        
        try {
            SEMAFORO_CONTADOR.acquire();
            //System.out.println("[remover 1] tentando acquire com o semáforo: " + semaforoGeral);
            SEMAFORO_GERAL.acquire();
            //System.out.println("[remover 2] acquire obtido e o semáforo: " + semaforoGeral);
            T item = FILA.removeFirst();
            SEMAFORO_GERAL.release();		
            //System.out.println("[remover 3] semáforo released: " + semaforoGeral);
            return item;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        } 

        return null;
    }
}