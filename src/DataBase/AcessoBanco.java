/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

/**
 *
 * @author a10391204
 */
import java.sql.*;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AcessoBanco {

    private Connection con;

    public Connection getCon() {
        return con;
    }
    private static AcessoBanco classeUnica;
    
    private AcessoBanco() throws SQLException{
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro no acesso banco");
        }
        con = DriverManager.getConnection("jdbc:sqlite:banco.db");
        Statement statement = con.createStatement();
        // criando uma tabela
        statement.execute("CREATE TABLE IF NOT EXISTS jogadores(nome VARCHAR, vitorias INTEGER, derrotas INTEGER, empates INTEGER)");
        
    }
    
    public static AcessoBanco getAcesso() throws SQLException{
        
         if(classeUnica == null){
             classeUnica = new AcessoBanco();
         }
           
         return classeUnica;
         
    }
    
    /*
    private static void connect() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            
            Statement statement = connection.createStatement();

            // criando uma tabela
            //statement.execute("CREATE TABLE IF NOT EXISTS RC_TEST(id VARCHAR, vitorias INTEGER, derrotas INTEGER, empates INTEGER)");
            
            // inserindo registros
            //statement.execute("INSERT INTO RC_TEST( ID, NOME) VALUES (1, 'Wolmir'), (2, 'Garbin')");

            // lendo os registros
            //PreparedStatement stmt = connection.prepareStatement("select * from RC_TEST");
            //ResultSet resultSet = stmt.executeQuery();
            
            while (resultSet.next()) {
                Integer id = resultSet.getInt("ID");
                String nome = resultSet.getString("NOME");

                System.out.println( id + " - " + nome);
            }
           

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    */
    

    
    public boolean executarQuery(String query){
        
        boolean saida = false;
        try(Statement statement = con.createStatement()){
            statement.execute(query);
           
            saida = true;
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        }
        return saida;
    }
    
}