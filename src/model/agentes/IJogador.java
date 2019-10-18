package model.agentes;

import java.io.Closeable;


public interface IJogador{
    
    public boolean iniciarPartida();
    public boolean desistirDeProcurarPartida();
    public boolean encerrarPartida();
    
    public void andarParaCima();
    public void andarParaBaixo();
    public void andarParaEsquerda();
    public void andarParaDireita();
    
    public double getVD();
    
    public void saindo();
 
}
