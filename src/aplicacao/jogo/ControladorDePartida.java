package aplicacao.jogo;

public interface ControladorDePartida {
    
    public void vocerPerdeu();
    public void voceGanhou();
    public void adversarioSaiu();
    
    public void irParaOHall();
    public void logar(String login); // AJUSTAR ISSO
    public void falhaAoLogar(String mensagem);
    
}
