package controller.auxiliares;

import controller.*;
import java.util.ArrayList;
import model.agentes.*;

public class IniciadorDePartidas implements Runnable {

    private ControladorGeral controladorGeral;
    boolean running = true;

    public IniciadorDePartidas(ControladorGeral controladorGeral) {
        this.controladorGeral = controladorGeral;
    }

    @Override
    public void run() {

        // TODO Auto-generated method stub
        while (running) {
            
                       
            ArrayList<IJogadorProtegido> novo = controladorGeral.getPar();
            iniciaPartida(novo.get(0), novo.get(1));
            
        }
    }

    private void iniciaPartida(IJogadorProtegido jogadorA, IJogadorProtegido jogadorB) {
        controladorGeral.iniciarPartidaPronta(jogadorA, jogadorB);
        
    }
}
