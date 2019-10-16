package stub;

import Logger.Logger;
import static Logger.Logger.Tipo.ERRO;
import model.agentes.ControladorDePartida;
import model.agentes.IJogador;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import model.send.Arena;
import stub.comando.Comando;
import stub.comando.ComandoExibirMensagem;
import stub.comando.gerenciador_de_udp.*;
import stub.comando.jogador.*;
import stub.comunicacao.Comunicador;
import stub.comunicacao.FilaMonitorada;

public class GerenciadorDeCliente extends Stub implements ControladorDePartida {
    
    private final IJogador JOGADOR;
    private final InetAddress ENDERECO_DO_SERVIDOR;
    private final GerenciadorDeConexaoUDPRemota GERENCIADOR_CONEXAO_UDP;
    
    public GerenciadorDeCliente(IJogador jogador, Socket socket) {
        super(Comunicador.Modo.SERVIDOR,
                socket.getInetAddress(),
                socket.getPort());
            
        this.JOGADOR = jogador;    
        this.ENDERECO_DO_SERVIDOR = socket.getInetAddress();
        this.GERENCIADOR_CONEXAO_UDP = new GerenciadorDeConexaoUDPRemota(this.MENSAGEIRO, this.ENDERECO_DO_SERVIDOR, this.INTERPRETADOR);
        this.INTERPRETADOR.cadastrarComandos(this.criarComandosNecessarios());
        
        super.iniciar(socket);
    }
    
    @Override
    public void receberMensagem(byte[] mensagem) {
        if(mensagem == null) {
            Logger.registrar(ERRO, new String[]{"GERENCIADOR_DE_CLIENTE"}, "Mensagem nula recebida. A mensagem sera ignorada.");
            return;
        }
        
        this.INTERPRETADOR.interpretar(mensagem);
    }
    
    
    /* ########################### CHAMADAS DE RPC ########################## */

    @Override
    public void vocerPerdeu() {
        byte[] mensagem = this.INTERPRETADOR.codificarVocerPerdeu();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }

    @Override
    public void voceGanhou() {
        byte[] mensagem = this.INTERPRETADOR.codificarVoceGanhou();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }

    @Override
    public void adversarioSaiu() {
        byte[] mensagem = this.INTERPRETADOR.codificarAdversarioSaiu();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }

    @Override
    public void irParaOHall() {
        byte[] mensagem = this.INTERPRETADOR.codificarIrParaOHall();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }

    @Override
    public void logar(String login) {
        byte[] mensagem = this.INTERPRETADOR.codificarLogar(login);
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }

    @Override
    public void falhaAoLogar(String mensagemTextual) {
        byte[] mensagem = this.INTERPRETADOR.codificarFalhaAoLogar(mensagemTextual);
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    @Override
    public void entregarQuadro(Arena arena) {
        byte[] mensagem = this.INTERPRETADOR.codificarEntregarQuadro(arena);
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    @Override
    protected LinkedList<Comando> criarComandosNecessarios() {
        
        LinkedList<Comando> listaDeComandos = new LinkedList<>();
        
        listaDeComandos.add(new ComandoExibirMensagem("exibirMensagem"));
        listaDeComandos.add(new AtenderPedidoInicioDeAberturaUDP("atenderPedidoInicioDeAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new ContinuarAberturaUDP("continuarAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new FecharConexaoUDP("fecharConexaoUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new IniciarFechamentoConexaoUDP("iniciarFechamentoConexaoUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new IniciarPedidoDeAberturaUDP("iniciarPedidoDeAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        
        listaDeComandos.add(new AndarParaBaixo("andarParaBaixo", this.JOGADOR));
        listaDeComandos.add(new AndarParaCima("andarParaCima", this.JOGADOR));
        listaDeComandos.add(new AndarParaDireita("andarParaDireita", this.JOGADOR));
        listaDeComandos.add(new AndarParaEsquerda("andarParaEsquerda", this.JOGADOR));
        listaDeComandos.add(new DesistirDeProcurarPartida("desistirDeProcurarPartida", this.JOGADOR));
        listaDeComandos.add(new EncerrarPartida("encerrarPartida", this.JOGADOR));
        listaDeComandos.add(new IniciarPartida("iniciarPartida", this.JOGADOR));
        
        listaDeComandos.add(new GetVD("getVD", this.JOGADOR));
        listaDeComandos.add(new GetLocalAtual("getLocalAtual", this.JOGADOR));
        listaDeComandos.add(new SetLocalAtual("setLocalAtual", this.JOGADOR));
        
        return listaDeComandos;
    }
    
    @Override
    protected void devolverRetorno(byte[] mensagemRetorno) {
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagemRetorno);
    }
}