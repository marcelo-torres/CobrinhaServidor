package controller.auxiliares;

import controller.*;
import java.util.ArrayList;
import model.agentes.*;

public class IniciadorDePartidas implements Runnable {

    private ControladorGeral controladorGeral;
    boolean running;

    public IniciadorDePartidas(ControladorGeral controladorGeral) {
        this.controladorGeral = controladorGeral;
    }

    @Override
    public void run() {

        // TODO Auto-generated method stub
        while (running) {
            ArrayList<IJogador> novo = controladorGeral.getPar();

        }
    }

    private void iniciaPartida(IJogador jogadorA, IJogador jogadorB) {
        controladorGeral.iniciarPartidaPronta(jogadorA, jogadorB);
        
    }
}
