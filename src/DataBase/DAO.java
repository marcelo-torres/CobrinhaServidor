/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a10391204
 */
public class DAO {
    private AcessoBanco acesso;
    private final Object lockerDerrotas = new Object();
    private final Object lockerVitorias = new Object();
    private final Object lockerEmpates = new Object();
    
    public DAO() throws SQLException{
        acesso = AcessoBanco.getAcesso();
    }
    
    public boolean inserirJogador(String nome){
        
        synchronized(acesso){
            
                       
            
            Connection con = acesso.getCon();
        
            String query = ("SELECT nome FROM jogadores WHERE nome = '" + nome + "' ;");
        
            ResultSet rs = null;

            Statement statement = null;
            
            try {
                statement = con.createStatement();
                rs = statement.executeQuery(query);
                
                                
                if(!rs.next()){
                    acesso.executarQuery("INSERT INTO jogadores(nome, vitorias, derrotas, empates) VALUES ('"+nome+"', 0, 0, 0) ;");
                    return true;
                }
                   
            } catch (SQLException ex) {
                
                ex.printStackTrace();
                
            }
            return false;
        }

    }
    
    public double getVD(String nome){
        
        
        double vd = -1;    
        
        inserirJogador(nome);
        Connection con = acesso.getCon();
        
        String query = ("SELECT nome, vitorias, derrotas FROM jogadores WHERE nome = '" + nome+ "';");
        
        ResultSet rs = null;
        Statement statement = null;
        try {
            statement = con.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
            
        }
        try {
            rs = statement.executeQuery(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
            
        }
            
        
        try {
            
            
            
            int vitorias = rs.getInt(2);
            int derrotas = rs.getInt(3);
            
           
            if(vitorias == 0){
                vd = vitorias;
            }
            else vd = ((double)vitorias) / ((double)derrotas);

        } catch (SQLException ex) {
           ex.printStackTrace();

        }
        return vd;
        

    }
    
    public void printaBanco(){
        Connection con = acesso.getCon();
        
        String query = ("SELECT * FROM jogadores;");
        
        ResultSet rs = null;
        Statement statement = null;
        try {
            statement = con.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
            
        }
        try {
            rs = statement.executeQuery(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
            
        }
            
        
        try {
            
            
            
            while(rs.next()){
                System.out.println("\nNome: " + rs.getString(1) + 
                        "; \nVitorias: " + rs.getString(2) + 
                        "; \nDerrotas: " + rs.getString(3)+ 
                        "; \nEmpates: " + rs.getString(4) + ".\n");
            }
            
            

        } catch (SQLException ex) {
           ex.printStackTrace();

        }
    }
    
    public boolean incrementaDerrota(String nome){
        synchronized(lockerDerrotas){
            
            
            Connection con = acesso.getCon();
        
            String query = ("SELECT nome, derrotas FROM jogadores WHERE nome = '" + nome + "';");
        
            
           
            try {
                
                Statement statement = con.createStatement();
                
                ResultSet rs = statement.executeQuery(query);
                
                int derrotas = rs.getInt(2);
                acesso.executarQuery("UPDATE jogadores SET derrotas = " + (derrotas + 1) + " WHERE nome = '" + nome + "';");
                return true;
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
    public boolean incrementaVitorias(String nome){
        synchronized(lockerVitorias){
            
            
            Connection con = acesso.getCon();
        
            String query = ("SELECT vitorias FROM jogadores WHERE nome = '" + nome + "';");
        
            ResultSet rs = null;
            Statement statement = null;
            try {
                statement = con.createStatement();
            } catch (SQLException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);

            }
            
            try {
                rs = statement.executeQuery(query);
            } catch (SQLException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);

            }
            
            
            try {
                int vitorias = rs.getInt("vitorias");
                acesso.executarQuery("UPDATE jogadores SET vitorias = " + (vitorias + 1) + " WHERE nome = '" + nome + "';");
                return true;
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
    public boolean incrementaEmpates(String nome){
        synchronized(lockerEmpates){
            
            
            Connection con = acesso.getCon();
        
            String query = ("SELECT empates FROM jogadores WHERE nome = '" + nome + "';");
        
            ResultSet rs = null;
            Statement statement = null;
            try {
                statement = con.createStatement();
            } catch (SQLException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);

            }
            
            try {
                rs = statement.executeQuery(query);
            } catch (SQLException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);

            }
            try {
                int empates = rs.getInt("empates");
                acesso.executarQuery("UPDATE jogadores SET empates = " + (empates + 1) + " WHERE nome = '" + nome + "';");
                return true;
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
}
