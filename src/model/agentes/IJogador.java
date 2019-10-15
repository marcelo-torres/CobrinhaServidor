package model.agentes;

import java.io.Closeable;
import localizacoes.ILocal;

public interface IJogador extends Closeable  {
    
    public void iniciarPartida();
    public void desistirDeProcurarPartida();
    public void encerrarPartida();
    
    public void andarParaCima();
    public void andarParaBaixo();
    public void andarParaEsquerda();
    public void andarParaDireita();
    
    public double getVD();
    
    public ILocal getLocalAtual();
	
    public void setLocalAtual(ILocal local);
	
    
    @Override
    public void close();
}
