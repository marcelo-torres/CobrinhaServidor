package Logger;

public class Logger {
    
    private static final boolean IMPRIMIR_MENSAGEM = true;
    
    public enum Tipo {
        ERRO("ERRO"),
        DEBUG("DEBUG"),
        INFO("INFO");
        
        private final String TIPO;
        
        Tipo(String valorOpcao){
            TIPO = valorOpcao;
        }
        
        public String getTIPO() {
            return this.TIPO;
        }
    }
    
    public static void registrar(Tipo tipo, String[] tags, String mensagem, Exception exception) {
        String classeException;
        if(exception == null) {
            classeException = "";
        } else {
            classeException = exception.getClass().toString();
        }

        String mensagemImpressao = ("[" + tipo.getTIPO() + "]"  + processarTags(tags) + " - " + mensagem + " (" + classeException + ")");
        escreverMensagem(mensagemImpressao);
    }
    
    public static void registrar(Tipo tipo, String[] tags, String mensagem) {
        String mensagemImpressao = ("[" + tipo.getTIPO() + "]" + processarTags(tags) + " - " + mensagem);
        escreverMensagem(mensagemImpressao);
    }
    
    public static void registrar(Tipo tipo, String mensagem, Exception exception) {
        String classeException;
        if(exception == null) {
            classeException = "";
        } else {
            classeException = exception.getClass().toString();
        }
        String mensagemImpressao = ("[" + tipo.getTIPO() + "] - " + mensagem + " (" + classeException + ")");
        escreverMensagem(mensagemImpressao);
    }
    
    public static void registrar(Tipo tipo, String mensagem) {
        String mensagemImpressao = ("[" + tipo.getTIPO() + "] - " + mensagem);
        //escreverMensagem(mensagemImpressao);
    }
    
    private static void escreverMensagem(String mensagem) {
        if(!IMPRIMIR_MENSAGEM) return;
        System.out.println("[LOG]" + mensagem);
    }
    
    private static String processarTags(String[] tags) {
        if(tags == null || tags.length == 0) {
            return "";
        }
        
        String aux = "";
        for(String tag : tags) {
            aux += "[" + tag + "]";
        }
        
        return aux;
    }
}
