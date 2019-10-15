package stub.comando.controlador_de_partida;

import stub.comando.Parametros;

public class LogarParametros extends Parametros {
    
    private String login;
    
    public void setLogin(String login) {
        this.login = login;
    }
    
    public String getLogin() {
        return login;
    }
}