package stub.comando;

import java.util.HashMap;

public abstract class Comando {
    
    private static final HashMap<String, Comando> COMANDOS = new HashMap();
    
    private static boolean cadastrarComando(String codigo, Comando comando) {
        Comando comandoObtido = COMANDOS.get(codigo);
        
        if(comandoObtido == null) {
            COMANDOS.put(codigo, comando);
            return true;
        } else {
            return false;
        }
    }
    
    private static void removerComando(Comando comando) {
        COMANDOS.remove(comando.getCodigo());
    }
    
    
    private final String CODIGO;
    
    public Comando(String codigo) {
        this.CODIGO = codigo;
        boolean sucesso = Comando.cadastrarComando(codigo, this);
        if(!sucesso) {
            //throw new IllegalArgumentException("Nao eh possivel criar o comando. O codigo " + codigo + " ja esta sendo usado por um comando");
        }
    }
    
    public String getCodigo() {
        return this.CODIGO;
    }
    
    public void removerDoCatalogo() {
        Comando.removerComando(this);
    }
    
    public abstract void executar();
    
    public abstract void definirParametros(String... parametros);
}
