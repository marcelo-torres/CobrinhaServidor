package cliente;

import java.net.Socket;
import localizacoes.Hall;
import stub.GerenciadorDeCliente;
import localizacoes.ILocal;
import model.send.Arena;
import model.agentes.IJogadorVisaoStubServidor;


public class RepresentanteJogador implements Runnable,  IJogadorVisaoStubServidor {
    
    private final GerenciadorDeCliente GerenciadorDeCliente;
    private final Socket SOCKET;
    
    public RepresentanteJogador(Socket socket) {
        this.SOCKET = socket;
        this.GerenciadorDeCliente = new GerenciadorDeCliente(socket);
    }


    

    private void pausar(int tempo) {
        try {
            new Thread().sleep(tempo);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean iniciarPartida() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: iniciarPartida");
        return true;
    }

    @Override
    public boolean desistirDeProcurarPartida() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: desistirDeProcurarPartida");
        return true;
    }

    @Override
    public boolean encerrarPartida() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: encerrarPartida");
        return true;
    }

    
    @Override
    public void andarParaCima() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: andarParaCima");
    }

    @Override
    public void andarParaBaixo() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: andarParaBaixo");
    }

    @Override
    public void andarParaEsquerda() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: andarParaEsquerda");
    }

    @Override
    public void andarParaDireita() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: andarParaDireita");
    }

    @Override
    public double getVD() {
        double valor = 666.0;
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: getVD -> retornando o valor: " + valor);
        return valor;
    }



    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        GerenciadorDeCliente.ganhou();
        GerenciadorDeCliente.perdeu();
        
        GerenciadorDeCliente.adversarioSaiu();
        GerenciadorDeCliente.exibirTelaSessao();
        //GerenciadorDeCliente.logar("<login_do_usuario>");//?? 
        GerenciadorDeCliente.falhaAoLogar("<mensagem_de_erro>");
        Arena arena = new Arena(10, 10, 5);
        System.out.println("Enviando quadro..." + arena);
        GerenciadorDeCliente.novoQuadro(arena);
    }

    @Override
    public void iniciarSessao(String nome_jogador) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void encerrarSessao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    
}
