package model.agentes;

import java.io.Closeable;

public interface Jogador extends Closeable  {
    
    public void iniciarPartida();
    public void desistirDeProcurarPartida();
    public void encerrarPartida();
    
    public void andarParaCima();
    public void andarParaBaixo();
    public void andarParaEsquerda();
    public void andarParaDireita();
    
    
    @Override
    public void close();
}
