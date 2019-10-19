package model.agentes;

public interface IJogadorVisaoStubServidor extends IJogadorProtegido{
    
    public void iniciarSessao(String nome_jogador);     
       
    public boolean iniciarPartida();
    public boolean desistirDeProcurarPartida();
    public boolean encerrarPartida();
    
    public void andarParaCima();
    public void andarParaBaixo();
    public void andarParaEsquerda();
    public void andarParaDireita();
    public void encerrarSessao();
    
 


 
}
