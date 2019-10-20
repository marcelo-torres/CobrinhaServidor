package teste;

import comunicacao_geral.GerenciadorDeRequisicao;
import java.net.Socket;
import model.send.Arena;
import stub.GerenciadorDeCliente;

public class GerenciadorDeConexoesTeste  implements GerenciadorDeRequisicao {

    @Override
    public void gerenciarRequisicao(Socket socket) {
        
        JogadorTeste jogador = new JogadorTeste();
        GerenciadorDeCliente gerenciadorDeCliente = new GerenciadorDeCliente(jogador, socket);
        gerenciadorDeCliente.iniciarStub();
        
        gerenciadorDeCliente.novoQuadro(new Arena(10, 10, 10));
        gerenciadorDeCliente.exibirTelaSessao();
        gerenciadorDeCliente.exibirTelaBusca();
        gerenciadorDeCliente.exibirTelaJogo();
        gerenciadorDeCliente.exibirTelaInicio();
    
        gerenciadorDeCliente.perdeu();
        gerenciadorDeCliente.ganhou();
        gerenciadorDeCliente.empatou();
        gerenciadorDeCliente.adversarioSaiu();
        gerenciadorDeCliente.falhaAoLogar("MENSAGEM DE FALHA AO LOGAR");
        gerenciadorDeCliente.falha("MENSAGEM DE FALHA");
    
        gerenciadorDeCliente.procurandoPartida();
    }
}