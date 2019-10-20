package stub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import model.send.Arena;
import stub.comando.Comando;

import stub.comando.ComandoExibirMensagemParametros;
import stub.comando.Parametros;
import stub.comando.controlador_de_partida.*;
import stub.comando.gerenciador_de_udp.AtenderPedidoInicioDeAberturaUDPParametros;
import stub.comando.gerenciador_de_udp.ContinuarAberturaUDPParametros;
import stub.comando.jogador.IniciarSessaoParametros;
import stub.comunicacao.FilaMonitorada;

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
    
    private final HashMap<String, FilaMonitorada> FILA_RETORNOS = new HashMap<>();
    private final HashMap<String, Comando> COMANDOS = new HashMap<>();
    
    private final Stub STUB;
    
    public Interpretador(Stub stub) {
        this.STUB = stub;
    }
    
    private static class PacoteDeChamadaRemota implements Serializable {
    
        public static enum Tipo {
            CHAMADA(1),
            RETORNO(2);
            
            private int COD;
            private Tipo(int codigo) {
                this.COD = codigo;
            }
        }
        
        public static PacoteDeChamadaRemota criarPacoteDeChamada(String nomeDoMetodo, Parametros parametros) {
            return new PacoteDeChamadaRemota(Tipo.CHAMADA, nomeDoMetodo, parametros, null);
        }
        
        public static PacoteDeChamadaRemota criarPacoteDeRetorno(String nomeDoMetodo, Object retorno) {
            return new PacoteDeChamadaRemota(Tipo.RETORNO, nomeDoMetodo, null, retorno);
        }
        
        private final Tipo TIPO;
        private final String NOME_DO_METODO;
        private final Parametros PARAMETROS;
        private final Object RETORNO;
        
        public PacoteDeChamadaRemota(Tipo tipo, String nomeDoMetodo, Parametros parametros, Object retorno) {
            this.TIPO = tipo;
            this.NOME_DO_METODO = nomeDoMetodo;
            this.PARAMETROS = parametros;
            this.RETORNO = retorno;
        }
        
        public Tipo getTipo() {
            return this.TIPO;
        }
        
        public String getNomeDoMetodo() {
            return this.NOME_DO_METODO;
        }
        
        public Parametros getParametros() {
            return this.PARAMETROS;
        }
        
        public Object getRetorno() {
            return this.RETORNO;
        }
    }
    
    private byte[] empacotarChamadaDeMetodo(String metodo) {
        return empacotarChamadaDeMetodo(metodo, new Parametros());
    }
    
    private byte[] empacotarChamadaDeMetodo(String metodo, Parametros parametros) {
        PacoteDeChamadaRemota pacote = PacoteDeChamadaRemota.criarPacoteDeChamada(metodo, parametros);
        return this.converterParaBytes(pacote);
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
    
    public void cadastrarFilaDeRetorno(String nomeMetodo, FilaMonitorada filaDeRetorno) {
        FilaMonitorada filaEncontrada = this.FILA_RETORNOS.get(nomeMetodo);
        if(filaEncontrada == null) {
            this.FILA_RETORNOS.put(nomeMetodo, filaDeRetorno);
        } else {
            throw new IllegalArgumentException("Ja existe uma fila pde retorno para o metodo " + nomeMetodo);
        }
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
        PacoteDeChamadaRemota pacoteDeChamadaRemota = (PacoteDeChamadaRemota) this.converterParaObjeto(mensagem);
        
        if(pacoteDeChamadaRemota.getTipo() == PacoteDeChamadaRemota.Tipo.RETORNO) {
            FilaMonitorada fila = this.FILA_RETORNOS.get(pacoteDeChamadaRemota.getNomeDoMetodo());
            if(fila != null) {
                fila.adicionar(pacoteDeChamadaRemota.getRetorno());
            } else {
                throw new RuntimeException("Nao foi possivel encontrar a fila de retorno para o comando com a chave " + pacoteDeChamadaRemota.getNomeDoMetodo() + " nao encontrado");
            }
        } else if(pacoteDeChamadaRemota.getTipo() == PacoteDeChamadaRemota.Tipo.CHAMADA) {
            Comando comando = this.COMANDOS.get(pacoteDeChamadaRemota.getNomeDoMetodo());
            
            if(comando != null) {
                Object retorno = comando.executar(pacoteDeChamadaRemota.getParametros());
                
                if(comando.getPossuiRetorno()) {
                    PacoteDeChamadaRemota pacote = PacoteDeChamadaRemota.criarPacoteDeRetorno(pacoteDeChamadaRemota.getNomeDoMetodo(), retorno);
                    byte[] mensagemRetorno = this.converterParaBytes(pacote);
                    this.STUB.devolverRetorno(mensagemRetorno);
                }
            } else {
                throw new RuntimeException("Comando com a chave " + pacoteDeChamadaRemota.getNomeDoMetodo() + " nao encontrado");
            }
        }
        
        /*Comando comando = this.COMANDOS.get(pacoteDeChamadaRemota.getNomeDoMetodo());
        if(comando != null) {
            Object retorno = comando.executar(pacoteDeChamadaRemota.getParametros());
            
            if(pacoteDeChamadaRemota.getTipo() == PacoteDeChamadaRemota.Tipo.RETORNO) {
                FilaMonitorada fila = this.FILA_RETORNOS.get(pacoteDeChamadaRemota.getNomeDoMetodo());
                if(fila != null) {
                    fila.adicionar(retorno);
                } else {
                    throw new RuntimeException("Nao foi possivel encontrar a fila de retorno para o comando com a chave " + pacoteDeChamadaRemota.getNomeDoMetodo() + " nao encontrado");
                }
            } else if(retorno != null) {
                PacoteDeChamadaRemota pacote = PacoteDeChamadaRemota.criarPacoteDeRetorno(pacoteDeChamadaRemota.getNomeDoMetodo(), retorno);
                byte[] mensagemRetorno = this.converterParaBytes(pacote);
                this.STUB.devolverRetorno(mensagemRetorno);
            }
        } else {
            throw new RuntimeException("Comando com a chave " + pacoteDeChamadaRemota.getNomeDoMetodo() + " nao encontrado");
        }*/
    }
    
    
    /* ###################################################################### */
    /* ################# CODIFICACAO DE CHAMADAS DE METODO ################## */
    /* ###################################################################### */
    
    /* ##################### COMANDO GERENCIADOR DE UDP ##################### */
    
    public byte[] codificarExibirMensagem(String mensagemTextual) {
        ComandoExibirMensagemParametros comandoExibirMensagemParametros = new ComandoExibirMensagemParametros();
        comandoExibirMensagemParametros.setMensagem(mensagemTextual);
        byte[] mensagem = this.empacotarChamadaDeMetodo("exibirMensagem", comandoExibirMensagemParametros);
        return mensagem;
    }
    
    public byte[] codificarAtenderPedidoInicioDeAberturaUDP(int portaUDPServidor) {
        AtenderPedidoInicioDeAberturaUDPParametros parametro = new AtenderPedidoInicioDeAberturaUDPParametros();
        parametro.setPortaUDPServidor(portaUDPServidor);
        byte[] mensagem = this.empacotarChamadaDeMetodo("atenderPedidoInicioDeAberturaUDP", parametro);
        return mensagem;
    }
    
    public byte[] codificarContinuarAberturaUDP(int portaUDPServidor) {
        ContinuarAberturaUDPParametros parametros = new ContinuarAberturaUDPParametros();
        parametros.setPortaUDPServidor(portaUDPServidor);
        byte[] mensagem = this.empacotarChamadaDeMetodo("continuarAberturaUDP", parametros);
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
    
    public byte[] codificarIniciarSessao(String nomeJogador) {
        IniciarSessaoParametros iniciarSessaoParametros = new IniciarSessaoParametros();
        iniciarSessaoParametros.setNomeJogador(nomeJogador);
        byte[] mensagem = this.empacotarChamadaDeMetodo("iniciarSessao", iniciarSessaoParametros);
        return mensagem;
    }
    
    public byte[] codificarIniciarPartida() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("iniciarPartida");
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
    
    
    public byte[] codificarAndarParaCima() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("andarParaCima");
        return mensagem;
    }
    
    public byte[] codificarAndarParaBaixo() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("andarParaBaixo");
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
    
    public byte[] codificarEncerrarSessao() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("encerrarSessao");
        return mensagem;
    }
    
    
    /* ################### COMANDO CONTROLADOR DE PARTIDA ################### */
    
    public byte[] codificarNovoQuadro(Arena arena) {
        NovoQuadroParametro parametros = new NovoQuadroParametro();
        parametros.setArena(arena);
        byte[] mensagem = this.empacotarChamadaDeMetodo("novoQuadro", parametros);
        return mensagem;
    }
    
    public byte[] codificarExibirTelaSessao() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("exibirTelaSessao");
        return mensagem;
    }
    
    public byte[] codificarExibirTelaBusca() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("exibirTelaBusca");
        return mensagem;
    }
    
    public byte[] codificarExibirTelaJogo() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("exibirTelaJogo");
        return mensagem;
    }
    
    public byte[] codificarExibirTelaInicio() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("exibirTelaInicio");
        return mensagem;
    }
    
    
    public byte[] codificarPerdeu() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("perdeu");
        return mensagem;
    }

    public byte[] codificarGanhou() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("ganhou");
        return mensagem;
    }
    
    public byte[] codificarEmpatou() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("empatou");
        return mensagem;
    }

    public byte[] codificarAdversarioSaiu() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("adversarioSaiu");
        return mensagem;
    }
    
    public byte[] codificarFalhaAoLogar(String mensagemTextual) {
        FalhaAoLogarParametros parametros = new FalhaAoLogarParametros();
        parametros.setMensagem(mensagemTextual);
        byte[] mensagem = this.empacotarChamadaDeMetodo("falhaAoLogar", parametros);
        return mensagem;
    }
    
    public byte[] codificarFalha(String mensagemTextual) {
        FalhaParametros parametros = new FalhaParametros();
        parametros.setMensagem(mensagemTextual);
        byte[] mensagem = this.empacotarChamadaDeMetodo("falha", parametros);
        return mensagem;
    }
    
    
    public byte[] codificarProcurandoPartida() {
        byte[] mensagem = this.empacotarChamadaDeMetodo("procurandoPartida");
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
        } catch (IOException ioe) {
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
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
}