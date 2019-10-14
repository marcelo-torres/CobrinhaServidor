package controller.auxiliares;
import controller.*;
import model.agentes.*;

public class IniciadorDePartidas implements Runnable {

	private ControladorGeral controladorGeral;
	
	
	public IniciadorDePartidas(ControladorGeral controladorGeral) {
		this.controladorGeral = controladorGeral;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	private void iniciaPartida(IJogador jogadorA, IJogador jogadorB) {
		
	}
}
