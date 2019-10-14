package model.agentes;

import java.io.Closeable;
import localizacoes.Local;

public interface IJogador extends Closeable  {
    
    public void iniciarPartida();
    public void desistirDeProcurarPartida();
    public void encerrarPartida();
    
    public void andarParaCima();
    public void andarParaBaixo();
    public void andarParaEsquerda();
    public void andarParaDireita();
    
    public double getVD();
    
    public Local getLocalAtual();
	
    public void setLocalAtual(Local local);
	
    
    @Override
    public void close();
}
