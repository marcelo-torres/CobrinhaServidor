package stub.comando;

public abstract class Comando {
           
    private final String CODIGO;
    
    public Comando(String codigo) {
        this.CODIGO = codigo;
    }
    
    public String getCodigo() {
        return this.CODIGO;
    }
    
    public abstract Object executar(Parametros parametros);
}
