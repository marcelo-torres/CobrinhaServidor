package aplicacao.jogo;

import java.io.Closeable;

public interface Jogador extends Closeable  {
    
    // DEFINIR RETORNOS
    
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
