package stub.comando;

public abstract class Comando {
           
    private final String CODIGO;
    private final boolean POSSUI_RETORNO;
    
    public Comando(String codigo, boolean possuiRetorno) {
        this.CODIGO = codigo;
        this.POSSUI_RETORNO = possuiRetorno;
    }
    
    public String getCodigo() {
        return this.CODIGO;
    }
    
    public boolean getPossuiRetorno() {
        return this.POSSUI_RETORNO;
    }
    
    public abstract Object executar(Parametros parametros);
}
