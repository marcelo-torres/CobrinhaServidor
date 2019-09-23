package comunicacao;

public interface EnviadorDeMensagem<T> {
    
    public void enviarMensagemTCP(T mensagem);
    
    public void enviarMensagemUDP(T mensagem);
    
}
