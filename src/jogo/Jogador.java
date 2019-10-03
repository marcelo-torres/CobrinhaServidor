package jogo;

public interface Jogador {
    
    // DEFINIR RETORNOS
    
    public void iniciarPartida();
    public void desistirDeProcurarPartida();
    public void encerrarPartida();
    
    public void andarParaCima();
    public void andarParaBaixo();
    public void andarParaEsquerda();
    public void andarParaDireita();
}
