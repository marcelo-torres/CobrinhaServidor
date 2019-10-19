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
            ArrayList<IJogadorProtegido> novo = controladorGeral.getPar();

        }
    }

    private void iniciaPartida(IJogadorVisaoStubServidor jogadorA, IJogadorVisaoStubServidor jogadorB) {
        controladorGeral.iniciarPartidaPronta(jogadorA, jogadorB);
        
    }
}
