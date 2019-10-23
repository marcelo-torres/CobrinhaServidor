/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.agentes;

import model.send.Arena;

/**
 *
 * @author Thiago
 */
public interface IControladorGeralVisaoAplicacaoServidor {
    public void novoQuadro(Arena arena);
    public void exibirTelaSessao();
    public void exibirTelaBusca();
    public void exibirTelaJogo();
    public void exibirTelaInicio();
    public void perdeu();
    public void ganhou();
    public void empatou();
    public void adversarioSaiu();
    public void falhaAoLogar(String mensagemTextual);
    public void falha(String nome_inválido);
    
}
