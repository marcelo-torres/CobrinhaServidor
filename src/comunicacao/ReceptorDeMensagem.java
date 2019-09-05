package comunicacao;

public interface ReceptorDeMensagem<T> {
    
    public void receberMensagem(T mensagem);
    
}
