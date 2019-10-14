package combinador.auxiliares;

import model.agentes.IJogador;

public class Capsula {
	
	private long timeStamp;
	private double vd;
	private IJogador jogador;
	private Capsula proximoTempo, anteriorTempo, proximoVD, anteriorVD;
	
	public Capsula(IJogador jogador) {
		if(jogador == null) return;
		this.jogador = jogador;
		this.timeStamp = System.currentTimeMillis(); 
		if(jogador != null) this.vd = jogador.getVD();		
	}
	
	public boolean isCabeca() {
		
		if(jogador != null) return false;
		else return true;
	}
	
	private Capsula() {
		
	}
	
	public static Capsula novaCabeca() {
		Capsula nova = new Capsula();
		
		nova.timeStamp = System.currentTimeMillis();
		return nova;
		
	}
	
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public Capsula getProximoTempo() {
		return proximoTempo;
	}
	public void setProximoTempo(Capsula proximoTempo) {
		this.proximoTempo = proximoTempo;
	}
	public Capsula getAnteriorTempo() {
		return anteriorTempo;
	}
	public void setAnteriorTempo(Capsula anteriorTempo) {
		this.anteriorTempo = anteriorTempo;
	}
	public Capsula getProximoVD() {
		return proximoVD;
	}
	public void setProximoVD(Capsula proximoVD) {
		this.proximoVD = proximoVD;
	}
	public Capsula getAnteriorVD() {
		return anteriorVD;
	}
	public void setAnteriorVD(Capsula anteriorVD) {
		this.anteriorVD = anteriorVD;
	}
	
	public double getVD() {
		return this.vd;
	}
	

	public Capsula expandir(double passosPorSegundos, long tempoAtual, int maxSegundos) {
		
		
		long tempoEmSegundos = (tempoAtual - timeStamp)/1000;
		
		Capsula anterior = this.getAnteriorVD().isCabeca() ? this.getAnteriorVD().getAnteriorVD() : this.getAnteriorVD();
		Capsula proximo = this.getProximoVD().isCabeca() ? this.getProximoVD().getProximoVD() : this.getProximoVD();
		
		if(anterior == this) return null;

		double distanciaAnterior = distancia(anterior);
		double distanciaProximo = distancia(proximo);
		
		Capsula maisPerto;
		double distanciaMaisPerto;
		
		if(distanciaAnterior > distanciaProximo) {
			maisPerto = proximo;
			distanciaMaisPerto = distanciaProximo;
		}
		else {
			maisPerto = anterior;
			distanciaMaisPerto = distanciaAnterior;
		}
		
		
		if(tempoEmSegundos >= maxSegundos) {
			/*
			return maisPerto.tirarDaFila();
			*/
			return maisPerto;
		}
		
		if(noRaio(distanciaMaisPerto, tempoEmSegundos * passosPorSegundos)) {
			/*
			return maisPerto.tirarDaFila();
			*/
			return maisPerto;
		}
		
		
		return null;
		
	}
	
	
	
	
	private boolean noRaio(double distancia, double distanciaAceitavel) {
		
		if(distancia <= distanciaAceitavel) return true;
		return false;
		
	}
	
	private double distancia(Capsula outra) {
		double distancia = this.getVD() - outra.getVD();		
		if(distancia > 0) return distancia;
		return distancia * -1;
		
	}
	
	public IJogador getJogador() {
		return this.jogador;
	}
	
	
	/*
	
	public Capsula tirarDaFila() {
		
		this.getAnteriorTempo().setProximoTempo(this.proximoTempo);
		this.getProximoTempo().setAnteriorTempo(this.anteriorTempo);
		this.getAnteriorVD().setProximoVD(this.proximoVD);
		this.getProximoVD().setAnteriorVD(this.anteriorVD);
		
		Jogador retorno = this;
		this.jogador = null;
		return retorno;
	}
	*/

}
