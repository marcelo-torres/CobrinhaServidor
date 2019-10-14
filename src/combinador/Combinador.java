package combinador;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import combinador.auxiliares.*;
import controller.*;

import model.agentes.IJogador;

public class Combinador implements Runnable {

	private EstruturaCombinador estrutura = new EstruturaCombinador();
	private Semaphore sleeperDoCombinador = new Semaphore(-1);
	private int quantidade;
	private int tempoDormindo;
	private boolean rodando = true;
	private Semaphore bloqueioContador = new Semaphore(1);
	

	private ControladorGeral cg; //confirmar o tipo depois
	
	public Combinador(ControladorGeral controladorGeral, int tempoDeEsperaPorCicloEmMS) {
		this.cg = controladorGeral;
		this.tempoDormindo = tempoDeEsperaPorCicloEmMS;
	}
	
	@Override
	public void run() {
		while(rodando) {
			try {
				sleeperDoCombinador.acquire();
				
                                LinkedList<ArrayList<IJogador>> lista = estrutura.listaDeParesFormados();
				
                                for(ArrayList<IJogador> jogadores : lista) {		
                                        cg.jogadoresCombinados(jogadores.get(0), jogadores.get(1));
				}
				Thread.sleep(this.tempoDormindo);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				renovaSemaforo();
			}
		}
	}
	
	public void finalizar() {
		this.rodando = false;
	}
	
	public void renovaSemaforo() {
		try {
			
			bloqueioContador.acquire();
			if(quantidade > 1) {
				sleeperDoCombinador = new Semaphore(1);
			}
			else if(quantidade > 0) {
				sleeperDoCombinador = new Semaphore(0);
			}
			else {
				sleeperDoCombinador = new Semaphore(-1);
			}
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		finally {
			bloqueioContador.release();
		}
	}

	public void incrementaQuant() {
		
		try {
			bloqueioContador.acquire();
			this.quantidade++;
			sleeperDoCombinador.release();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		finally {
			bloqueioContador.release();
		}
		
	}
	
	public void decrementaQuant() {
		
		try {
			bloqueioContador.acquire();
			this.quantidade--;
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		finally {
			bloqueioContador.release();
		}
		
	}
	
	public void inserir(IJogador jogador) {
		estrutura.inserir(jogador);
		incrementaQuant();
	}
	
	public boolean remover(IJogador jogador) {
		if(estrutura.remover(jogador)) {
			decrementaQuant();
			return true;
		}
		return false;
	}
}
