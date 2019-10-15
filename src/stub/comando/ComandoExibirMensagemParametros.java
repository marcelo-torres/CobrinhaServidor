package stub.comando;

public class ComandoExibirMensagemParametros extends Parametros {
    
   private String mensagem;
   
   public void setMensagem(String mensagem) {
       this.mensagem = mensagem;
   }
   
   public String getMensagem() {
       return this.mensagem;
   }
    
}
