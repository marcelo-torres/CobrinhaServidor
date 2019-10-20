package teste;

import Logger.Logger;
import model.agentes.IJogadorVisaoStubServidor;

/**
 *
 * @author marcelo
 */
public class JogadorTeste implements IJogadorVisaoStubServidor {

    @Override
    public void iniciarSessao(String nome_jogador) {
        Logger.registrar(Logger.Tipo.INFO, new String[]{" ======== TESTE ======== ","JOGADOR"}, "chamada iniciarSessao() | parametro: " + nome_jogador);
    }

    @Override
    public boolean iniciarPartida() {
        Logger.registrar(Logger.Tipo.INFO, new String[]{" ======== TESTE ======== ","JOGADOR"}, "chamada iniciarPartida() | retornando true");
        return true;
    }

    @Override
    public boolean desistirDeProcurarPartida() {
        Logger.registrar(Logger.Tipo.INFO, new String[]{" ======== TESTE ======== ","JOGADOR"}, "chamada desistirDeProcurarPartida() | retornando true");
        return true;
    }

    @Override
    public boolean encerrarPartida() {
        Logger.registrar(Logger.Tipo.INFO, new String[]{" ======== TESTE ======== ","JOGADOR"}, "chamada encerrarPartida() | retornando true");
        return true;
    }

    @Override
    public void andarParaCima() {
        Logger.registrar(Logger.Tipo.INFO, new String[]{" ======== TESTE ======== ","JOGADOR"}, "chamada andarParaCima()");
    }

    @Override
    public void andarParaBaixo() {
        Logger.registrar(Logger.Tipo.INFO, new String[]{" ======== TESTE ======== ","JOGADOR"}, "chamada andarParaBaixo()");
    }

    @Override
    public void andarParaEsquerda() {
        Logger.registrar(Logger.Tipo.INFO, new String[]{" ======== TESTE ======== ","JOGADOR"}, "chamada andarParaEsquerda()");
    }

    @Override
    public void andarParaDireita() {
        Logger.registrar(Logger.Tipo.INFO, new String[]{" ======== TESTE ======== ","JOGADOR"}, "chamada andarParaDireita()");
    }

    @Override
    public void encerrarSessao() {
        Logger.registrar(Logger.Tipo.INFO, new String[]{" ======== TESTE ======== ","JOGADOR"}, "chamada encerrarSessao()");
    }

    @Override
    public double getVD() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
