/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import model.send.Arena;

/**
 *
 * @author Thiago
 */
public interface ControladorGeralJogador {

    public void entregarQuadro(Arena arena);
    public void perdeu();
    public void ganhou();
    public void empatou();
    public void adversarioSaiu();
    public void falhaAoLogar(String mensagemTextual);
    public void irParaOHall();

    public void falha(String nome_inválido);
    public void partidaIniciada();
    public void procurandoPartida();
    
}
