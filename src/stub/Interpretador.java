package stub;

import Logger.Logger;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import stub.comando.Comando;

public class Interpretador implements Closeable {
    
    private final HashMap<String, Comando> COMANDOS = new HashMap<>();
    
    private byte[] empacotarChamadaDeMetodo(String metodo, String... parametrosDoMetodo) {
        JSONObject mensagem = new JSONObject();
        
        JSONArray parametros = new JSONArray();
        for(String parametro : parametrosDoMetodo) {
            parametros.put(parametro);
        }
        
        JSONObject chamadaDeMetodo = new JSONObject();
        chamadaDeMetodo.put("nome", metodo);
        chamadaDeMetodo.put("parametros", parametros);
        
        mensagem.put("chamada_de_metodo", chamadaDeMetodo);
        
        return mensagem.toString().getBytes();
    }
    
    public void cadastrarComandos(LinkedList<Comando> listaDeComandos) {
        for(Comando comando : listaDeComandos) {
            this.cadastrarComando(comando);
        }
    }
    
    public void cadastrarComando(Comando comando) {
        Comando objetoEncontrado = this.COMANDOS.get(comando.getCodigo());
        
        if(objetoEncontrado != null) {
            throw new IllegalArgumentException("Comando ja cadastrado");
        }
        
        this.COMANDOS.put(comando.getCodigo(), comando);
    }
    
    private void removerComandosDoCatalogo() {
        Set<String> chaves = this.COMANDOS.keySet();
        Iterator<String> iterador = chaves.iterator();
        while(iterador.hasNext()) {
            String chave = iterador.next();
            Comando comando = this.COMANDOS.get(chave);
            if(comando != null) {
                comando.removerDoCatalogo();
            } else {
                Logger.registrar(Logger.Tipo.ERRO, new String[]{"INTERPRETADOR"}, "Nao foi possivel encontrar o comando associado a chave " + chave + " para remove-lo do catalogo");
            }
        }
    }
    
    @Override
    public void close() {
        this.removerComandosDoCatalogo();
    }
    
    
    public void interpretar(byte[] mensagem) {
        String mensagemTextual = new String(mensagem);
        JSONObject mensagemJSON = new JSONObject(mensagemTextual);
        
        JSONObject chamadaDeMetodoJSON = mensagemJSON.getJSONObject("chamada_de_metodo");
        JSONArray parametrosJSON = chamadaDeMetodoJSON.getJSONArray("parametros");
        
        String chamadaDeMetodo = chamadaDeMetodoJSON.getString("nome");
        String[] parametros = this.extrairParametros(parametrosJSON);
        
        Comando comando = this.COMANDOS.get(chamadaDeMetodo);
        if(comando == null) {
            throw new RuntimeException("O interpretador nao pode interpretador o comando: \"" + chamadaDeMetodo + "\" comando nao cadastrado");
        }
        
        comando.definirParametros(parametros);
        comando.executar();
    }
    
    private String[] extrairParametros(JSONArray JSONArray) {
        String[] vetor = new String[JSONArray.length()];
        for (int i = 0; i < JSONArray.length(); i++) {
            vetor[i] = JSONArray.get(i).toString();
        }
        
        return vetor;
    }
    
    
    public byte[] codificarExibirMensagem(String mensagemTextual) {
        byte[] mensagem = this.empacotarChamadaDeMetodo("exibirMensagem" , mensagemTextual);
        return mensagem;
    }
    
    public byte[] codificarAtenderPedidoInicioDeAberturaUDP(int portaUDPServidor) {
        String arg0 = String.valueOf(portaUDPServidor);
        byte[] mensagem = this.empacotarChamadaDeMetodo("atenderPedidoInicioDeAberturaUDP" , arg0);
        return mensagem;
    }
    
    public byte[] codificarContinuarAberturaUDP(int portaUDPServidor) {
        String arg0 = String.valueOf(portaUDPServidor);
        byte[] mensagem = this.empacotarChamadaDeMetodo("continuarAberturaUDP", arg0);
        return mensagem;
    }
}