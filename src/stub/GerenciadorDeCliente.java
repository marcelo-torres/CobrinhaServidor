package stub;

import Logger.Logger;
import static Logger.Logger.Tipo.ERRO;

import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import model.send.Arena;
import stub.comando.Comando;
import stub.comando.ComandoExibirMensagem;
import model.agentes.IControladorGeralVisaoAplicacaoServidor;
import model.agentes.IJogadorVisaoStubServidor;
import stub.comando.gerenciador_de_udp.*;
import stub.comando.jogador.*;
import stub.comunicacao.Comunicador;
import stub.comunicacao.GerenciadorDePortas;


public class GerenciadorDeCliente extends Stub implements IControladorGeralVisaoAplicacaoServidor {
    
    private final IJogadorVisaoStubServidor JOGADOR;
    private final InetAddress ENDERECO_DO_SERVIDOR;
    private final GerenciadorDeConexaoUDPRemota GERENCIADOR_CONEXAO_UDP;
    private final Socket SOCKET;
    
    public GerenciadorDeCliente(IJogadorVisaoStubServidor jogador, Socket socket, GerenciadorDePortas gerenciadorDePortas) {
        super(Comunicador.Modo.SERVIDOR,
                gerenciadorDePortas,
                socket.getInetAddress(),
                socket.getPort());
        
        this.JOGADOR = jogador;
        this.SOCKET = socket;
        
        this.ENDERECO_DO_SERVIDOR = socket.getInetAddress();
        this.GERENCIADOR_CONEXAO_UDP = new GerenciadorDeConexaoUDPRemota(this.MENSAGEIRO, this.ENDERECO_DO_SERVIDOR, this.INTERPRETADOR, gerenciadorDePortas);
        this.INTERPRETADOR.cadastrarComandos(this.criarComandosNecessarios());  
    }
    
    public void iniciarStub() {
        super.iniciar(this.SOCKET);
    }
    
    @Override
    public void receberMensagem(byte[] mensagem) {
        if(mensagem == null) {
            Logger.registrar(ERRO, new String[]{"GERENCIADOR_DE_CLIENTE"}, "Mensagem nula recebida. A mensagem sera ignorada.");
            return;
        }
        
        this.INTERPRETADOR.interpretar(mensagem);
    }
    
    @Override
    protected void devolverRetorno(byte[] mensagemRetorno) {
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagemRetorno);
    }
    
    /* ########################### CHAMADAS DE RPC ########################## */

    @Override
    public void novoQuadro(Arena arena) {
        byte[] mensagem = this.INTERPRETADOR.codificarNovoQuadro(arena);
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
   @Override
    public void exibirTelaSessao() {
        byte[] mensagem = this.INTERPRETADOR.codificarExibirTelaSessao();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    @Override
    public void exibirTelaBusca() {
        byte[] mensagem = this.INTERPRETADOR.codificarExibirTelaBusca();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }

    @Override
    public void exibirTelaJogo() {
        byte[] mensagem = this.INTERPRETADOR.codificarExibirTelaJogo();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    @Override
    public void exibirTelaInicio() {
        byte[] mensagem = this.INTERPRETADOR.codificarExibirTelaInicio();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    
    @Override
    public void perdeu() {
        byte[] mensagem = this.INTERPRETADOR.codificarPerdeu();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }

    @Override
    public void ganhou() {
        byte[] mensagem = this.INTERPRETADOR.codificarGanhou();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    @Override
    public void empatou() {
        byte[] mensagem = this.INTERPRETADOR.codificarEmpatou();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    @Override
    public void adversarioSaiu() {
        byte[] mensagem = this.INTERPRETADOR.codificarAdversarioSaiu();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    @Override
    public void falhaAoLogar(String mensagemTextual) {
        byte[] mensagem = this.INTERPRETADOR.codificarFalhaAoLogar(mensagemTextual);
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    @Override
    public void falha(String stringFalha) {
        byte[] mensagem = this.INTERPRETADOR.codificarFalha(stringFalha);
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    @Override
    public void procurandoPartida() {
        byte[] mensagem = this.INTERPRETADOR.codificarProcurandoPartida();
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    
    @Override
    protected LinkedList<Comando> criarComandosNecessarios() {
        
        LinkedList<Comando> listaDeComandos = new LinkedList<>();
        
        listaDeComandos.add(new IniciarSessao("iniciarSessao", this.JOGADOR));
        listaDeComandos.add(new IniciarPartida("iniciarPartida", this.JOGADOR));
        listaDeComandos.add(new DesistirDeProcurarPartida("desistirDeProcurarPartida", this.JOGADOR));
        listaDeComandos.add(new EncerrarPartida("encerrarPartida", this.JOGADOR));
        
        listaDeComandos.add(new AndarParaCima("andarParaCima", this.JOGADOR));
        listaDeComandos.add(new AndarParaBaixo("andarParaBaixo", this.JOGADOR));
        listaDeComandos.add(new AndarParaEsquerda("andarParaEsquerda", this.JOGADOR));
        listaDeComandos.add(new AndarParaDireita("andarParaDireita", this.JOGADOR));
        listaDeComandos.add(new EncerrarSessao("encerrarSessao", this.JOGADOR));
        
        // Mensagem de debug
        listaDeComandos.add(new ComandoExibirMensagem("exibirMensagem"));
        
        // Comandos do gerenciador de UDP
        listaDeComandos.add(new AtenderPedidoInicioDeAberturaUDP("atenderPedidoInicioDeAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new ContinuarAberturaUDP("continuarAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new FecharConexaoUDP("fecharConexaoUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new IniciarFechamentoConexaoUDP("iniciarFechamentoConexaoUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new IniciarPedidoDeAberturaUDP("iniciarPedidoDeAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        
        return listaDeComandos;
    }

    
    




}