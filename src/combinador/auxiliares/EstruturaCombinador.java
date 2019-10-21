package combinador.auxiliares;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import model.agentes.IJogadorProtegido;


public class EstruturaCombinador {
	
	private Capsula cabeca = Capsula.novaCabeca();
	private HashMap<IJogadorProtegido, Capsula> mapaCapsulas = new HashMap<>();
	private HashMap<IJogadorProtegido, Capsula> mapaEntrada = new HashMap<>();
	private double passosPorSegundos = 0.1;
	private int maxSegundos = 50;
	
	//Semaforos internos e externos, para dar prioridade ao combinador.
	//Combinador s� concorre pelos internos.
	private Semaphore semInternoMapaEntrada = new Semaphore(1, true);
	private Semaphore semInternoEstruturaPrincipal = new Semaphore(1, true);
	private Semaphore semExternoMapaEntrada = new Semaphore(1);
	private Semaphore semExternoEstruturaPrincipal = new Semaphore(1);	
	
	public void inserir(IJogadorProtegido jogador) {
		if(jogador == null) return;
		
		Capsula nova = new Capsula(jogador);
		int acquired = 0;
		try {
			semExternoMapaEntrada.acquire();
			acquired++;
			semInternoMapaEntrada.acquire();
			acquired++;
			mapaEntrada.put(jogador, nova);
                        //System.out.println("ganhou todos");
			
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		finally {
			if(acquired > 0) {
				semExternoMapaEntrada.release();
                                //System.out.println("liberou externo");
			}
			if(acquired > 1) {
				semInternoMapaEntrada.release();
                                //System.out.println("liberou todos");
			}
                        
		}
	}
	
	private void transferir() { // tranfere os novos jogadores para a estrutura principal
		
                
		for(IJogadorProtegido j: mapaEntrada.keySet()) {
			inserirPrincipal(mapaEntrada.get(j));
		}
                mapaEntrada.clear();
	}
	
	private boolean removerMapaInicial(IJogadorProtegido jogador) {
		
		int acquired = 0;
		try {
			semExternoMapaEntrada.acquire();
			acquired++;
			semInternoMapaEntrada.acquire();
			acquired++;
			
			Capsula c = mapaEntrada.remove(jogador);
			if(c == null) return false;
			return true;
			
		} catch (InterruptedException e) {

			e.printStackTrace();
			return false;
		}
		finally {
			if(acquired > 0) {
				semExternoMapaEntrada.release();
			}
			if(acquired > 1) {
				semInternoMapaEntrada.release();
			}
		}
		
	}
	
	public boolean remover(IJogadorProtegido jogador) {
						
		if(removerMapaInicial(jogador)) return true;
		
		int acquired = 0;
		try {
			semExternoEstruturaPrincipal.acquire();
			acquired++;
			semInternoEstruturaPrincipal.acquire();
			acquired++;
			
			Capsula saindo = mapaCapsulas.remove(jogador);
			
			if(saindo == null) return false;
			
			saindo.getAnteriorTempo().setProximoTempo(saindo.getProximoTempo());
			saindo.getProximoTempo().setAnteriorTempo(saindo.getAnteriorTempo());
			
			saindo.getAnteriorVD().setProximoVD(saindo.getProximoVD());
			saindo.getProximoVD().setAnteriorVD(saindo.getAnteriorVD());
			
			return true;
			
		} catch (InterruptedException e) {

			e.printStackTrace();
			return false;
		}
		finally {
			if(acquired > 0) {
				semExternoEstruturaPrincipal.release();
			}
			if(acquired > 1) {
				semInternoEstruturaPrincipal.release();
			}
		}
	}
	
	private void inserirPrincipal(Capsula capsula) {
		
			Capsula atual;
			
			//insere na fila ordenada por tempo
			atual = cabeca.getProximoTempo();

			while(capsula.getTimeStamp() <= atual.getTimeStamp() && atual != cabeca) { 
			//menor TS = mais velho, para quando achar um mais velho
			//cabeca sempre entra primeiro, entao sempre mais velha
			//mais velho da estrutura do anterior at� o atual
				
				atual = atual.getProximoTempo();
			}
			
			capsula.setAnteriorTempo(atual.getAnteriorTempo());
			capsula.setProximoTempo(atual);
			
			capsula.getAnteriorTempo().setProximoTempo(capsula);
			capsula.getProximoTempo().setAnteriorTempo(capsula);
			
			atual = cabeca.getProximoVD();
			
			while(atual != cabeca && capsula.getVD() <= atual.getVD() ) {
				atual = atual.getProximoTempo();
			}
			
			capsula.setAnteriorVD(atual.getAnteriorVD());
			capsula.setProximoVD(atual);
			
			capsula.getAnteriorVD().setProximoVD(capsula);
			capsula.getProximoVD().setAnteriorVD(capsula);
			
			mapaCapsulas.put(capsula.getJogador(), capsula);
	}
	
	public LinkedList<ArrayList<IJogadorProtegido>> listaDeParesFormados(){
		
		LinkedList<ArrayList<IJogadorProtegido>> lista = new LinkedList<ArrayList<IJogadorProtegido>>();
		
		int acquired = 0;
		try {
                    
			semInternoEstruturaPrincipal.acquire();
			acquired++;
                        
			semInternoMapaEntrada.acquire();
			acquired++;
			
			transferir();
			
			semInternoMapaEntrada.release();
			acquired--;
			
			long tempoAtual = System.currentTimeMillis();
			Capsula atualBusca = cabeca.getAnteriorTempo();
			
                        
			while(!atualBusca.isCabeca()) {
				Capsula alcancada = atualBusca.expandir(passosPorSegundos, tempoAtual, maxSegundos);
				
				if(alcancada == null) { // N�o alca�ou ninguem
					atualBusca = atualBusca.getAnteriorTempo();
					continue;
				}
				
				//Alcan�ou alguem
				
				ArrayList<IJogadorProtegido> novoPar = new ArrayList<>();
				novoPar.add(atualBusca.getJogador());
				novoPar.add(alcancada.getJogador());
				
				Capsula aux; 
				
				if(alcancada.getProximoTempo() == atualBusca) {
					
					aux = alcancada.getAnteriorTempo();
				}else {
					aux = atualBusca.getAnteriorTempo();
				}
				
				removerParFormado(atualBusca, alcancada);
				
				atualBusca = aux;
				
				lista.add(novoPar);
			}
			
			return lista;
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			return null;
		}
		finally {
			if(acquired > 0) {
				semInternoEstruturaPrincipal.release();
			}
			if(acquired > 1) {
				semInternoMapaEntrada.release();
			}
		}
	}
	
	private void removerParFormado(Capsula a, Capsula b) {

		Capsula saindo = a;
		
		saindo.getAnteriorTempo().setProximoTempo(saindo.getProximoTempo());
		saindo.getProximoTempo().setAnteriorTempo(saindo.getAnteriorTempo());
		
		saindo.getAnteriorVD().setProximoVD(saindo.getProximoVD());
		saindo.getProximoVD().setAnteriorVD(saindo.getAnteriorVD());
		
		mapaCapsulas.remove(saindo.getJogador());
		
		saindo = b;
		
		saindo.getAnteriorTempo().setProximoTempo(saindo.getProximoTempo());
		saindo.getProximoTempo().setAnteriorTempo(saindo.getAnteriorTempo());
		
		saindo.getAnteriorVD().setProximoVD(saindo.getProximoVD());
		saindo.getProximoVD().setAnteriorVD(saindo.getAnteriorVD());
		
		mapaCapsulas.remove(saindo.getJogador());
	}
}
