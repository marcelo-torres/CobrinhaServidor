package stub;

import model.send.Arena;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONObject;
import stub.comando.Comando;

/**
 * Realiza o trabalho de interpretar mensagens recebidas no formato de vetor de
 * bytes, isto eh, traduz a mensagem para JSON e interpreta qual acao deve ser
 * executada. As acoes sao executadas atraves do padrao de projetos Comando. O
 * stub associado deve instanciar e cadastrar os objetos Comando que serao
 * utilizados.
 * 
 * Tambem realiza o trabalho de traducao de mensagens que serao enviadas.
 */
public class Interpretador {
    
    private final Charset CHARSET_PADRAO = Charset.forName("UTF-8");
    private final HashMap<String, Comando> COMANDOS = new HashMap<>();
    
    private byte[] empacotarChamadaDeMetodoComParametroObjeto(String metodo, Object vetorDeObjetos) {
        JSONObject mensagem = new JSONObject();
        
        JSONObject chamadaDeMetodo = new JSONObject();
        chamadaDeMetodo.put("nome", metodo);
        chamadaDeMetodo.put("parametros", vetorDeObjetos);
        
        mensagem.put("chamada_de_metodo", chamadaDeMetodo);
        
        return mensagem.toString().getBytes();
    }
    
    private byte[] empacotarChamadaDeMetodo(String metodo, String... parametrosDoMetodo) {
        JSONObject mensagem = new JSONObject();
        
        JSONArray parametros = new JSONArray();
        if(parametrosDoMetodo != null) {
            for(String parametro : parametrosDoMetodo) {
                parametros.put(parametro);
            }
        }
        
        JSONObject chamadaDeMetodo = new JSONObject();
        chamadaDeMetodo.put("nome", metodo);
        chamadaDeMetodo.put("parametros", parametros);
        
        mensagem.put("chamada_de_metodo", chamadaDeMetodo);
        
        return mensagem.toString().getBytes();
    }
    
    /**
     * Registra uma lista de comandos em uma estrutura interna. Quando o
     * interpretador interpretar uma mensagem ordenando a execucao do metodo
     * comando correspondente, este metodo sera encontrado dentro desta estrutura 
     * interna.
     * 
     * @param listaDeComandos Lista de objetos Comando a serem armazenados
     */
    public void cadastrarComandos(LinkedList<Comando> listaDeComandos) {
        for(Comando comando : listaDeComandos) {
            this.cadastrarComando(comando);
        }
    }
    
    /**
     * Registra um comando em uma estrutura interna. Quando o interpretador
     * interpretar uma mensagem ordenando a execucao do metodo comando 
     * correspondente, este metodo sera encontrado dentro desta estrutura 
     * interna.
     * 
     * @param comando Comando a ser armazenado.
     */
    public void cadastrarComando(Comando comando) {
        Comando objetoEncontrado = this.COMANDOS.get(comando.getCodigo());
        
        if(objetoEncontrado != null) {
            throw new IllegalArgumentException("Comando ja cadastrado");
        }
        
        this.COMANDOS.put(comando.getCodigo(), comando);
    }
    
    /**
     * Transforma uma mensagem em uma String UTF-8 que esta no formato JSON,
     * entao interpreta o nome do metodo a ser chamado e os paramentros e define
     * os parametros e executa o objeto responsavel por chamar o metodo
     * correspondente.
     * 
     * @param mensagem Mensagem a ser interpretada
     */
    public void interpretar(byte[] mensagem) {
        String mensagemTextual = new String(mensagem, this.CHARSET_PADRAO);
        JSONObject mensagemJSON = new JSONObject(mensagemTextual);
        
        JSONObject chamadaDeMetodoJSON = mensagemJSON.getJSONObject("chamada_de_metodo");
        JSONArray parametrosJSON = chamadaDeMetodoJSON.getJSONArray("parametros");
        
        String chamadaDeMetodo = chamadaDeMetodoJSON.getString("nome");
        String[] parametros = this.extrairParametros(parametrosJSON);
        
        Comando comando = this.COMANDOS.get(chamadaDeMetodo);
        if(comando == null) {
            throw new RuntimeException("O interpretador nao pode interpretador o comando: \"" + chamadaDeMetodo + "\" comando nao cadastrado");
        }
        
        if(parametros.length > 0) {
            comando.definirParametros(parametros);
        }
        comando.executar();
    }
    
    private String[] extrairParametros(JSONArray JSONArray) {
        String[] vetor = new String[JSONArray.length()];
        for (int i = 0; i < JSONArray.length(); i++) {
            vetor[i] = JSONArray.get(i).toString();
        }
        
        return vetor;
    }
    
    
    /* ###################################################################### */
    /* ################# CODIFICACAO DE CHAMADAS DE METODO ################## */
    /* ###################################################################### */
    
    /* ##################### COMANDO GERENCIADOR DE UDP ##################### */
    
    public byte[] codificarExibirMensagem(String mensagemTextual) {
        byte[] mensagem = this.empacotarChamadaDeMetodo("exibirMensagem", mensagemTextual);
        return mensagem;
    }
    
    public byte[] codificarAtenderPedidoInicioDeAberturaUDP(int portaUDPServidor) {
        String arg0 = String.valueOf(portaUDPServidor);
        byte[] mensagem = this.empacotarChamadaDeMetodo("atenderPedidoInicioDeAberturaUDP", arg0);
        return mensagem;
    }
    
    public byte[] codificarContinuarAberturaUDP(int portaUDPServidor) {
        String arg0 = String.valueOf(portaUDPServidor);
        byte[] mensagem = this.empacotarChamadaDeMetodo("continuarAberturaUDP", arg0);
        return mensagem;
    }
    
    public byte[] codificarIniciarFechamentoConexaoUDP() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("iniciarFechamentoConexaoUDP");
        return mensagem;
    }
    
    public byte[] codificarFecharConexaoUDP() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("fecharConexaoUDP");
        return mensagem;
    }
    
    
    /* ########################## COMANDO JOGADOR ########################### */
    
    public byte[] codificarAndarParaBaixo() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("andarParaBaixo");
        return mensagem;
    }
    
    public byte[] codificarAndarParaCima() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("andarParaCima");
        return mensagem;
    }
    
    public byte[] codificarAndarParaDireita() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("andarParaDireita");
        return mensagem;
    }
    
    public byte[] codificarAndarParaEsquerda() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("andarParaEsquerda");
        return mensagem;
    }
    
    public byte[] codificarDesistirDeProcurarPartida() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("desistirDeProcurarPartida");
        return mensagem;
    }
    
    public byte[] codificarEncerrarPartida() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("encerrarPartida");
        return mensagem;
    }
    
    public byte[] codificarIniciarPartida() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("iniciarPartida");
        return mensagem;
    }
    
    
    /* ################### COMANDO CONTROLADOR DE PARTIDA ################### */
    
    public byte[] codificarVocerPerdeu() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("vocerPerdeu");
        return mensagem;
    }

    public byte[] codificarVoceGanhou() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("voceGanhou");
        return mensagem;
    }

    public byte[] codificarAdversarioSaiu() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("adversarioSaiu");
        return mensagem;
    }

    public byte[] codificarIrParaOHall() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("irParaOHall");
        return mensagem;
    }

    public byte[] codificarLogar(String login) {
        byte[] mensagem = this.empacotarChamadaDeMetodo("logar");
        return mensagem;
    }

    public byte[] codificarFalhaAoLogar(String mensagemTextual) {
        byte[] mensagem = this.empacotarChamadaDeMetodo("falhaAoLogar");
        return mensagem;
    }
    
    public byte[] codificarEntregarQuadro(Arena arena) {
        byte[] bytes = this.converterParaBytes(arena);
        byte[] mensagem = this.empacotarChamadaDeMetodoComParametroObjeto("entregarQuadro", bytes);
        return mensagem;
    }
    
    
    /* ###################### METODOS DE SERIALIZACAO ####################### */
    
    // Retirado de <https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array>
    public byte[] converterParaBytes(Serializable objeto) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);   
            out.writeObject(objeto);
            out.flush();
            byte[] yourBytes = bos.toByteArray();
            return yourBytes;
        } catch(IOException ioe) {
            return null;
        } finally {
            try {
              bos.close();
            } catch (IOException ex) {
              // ignore close exception
            }
        }
    }
    
    // Retirado de <https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array>
    public Object converterParaObjeto(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object objeto = in.readObject();
            return objeto;
        } catch(IOException ioe) {
            return null;
        } catch(ClassNotFoundException cnfe) {
            return null;
        }finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException ex) {
              // ignore close exception
            } 
        }
    }
}