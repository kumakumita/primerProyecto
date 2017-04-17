/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
create table personas 
            ( id int auto_increment primary key
			 ,nombre varchar(15)
             , Altura numeric(4,2)
             , peso numeric(5,2)
             , fecNac date)
*/
package jtablepersonasimcbdr;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JOptionPane;
/**
 *
 * @author DiurnoF
 */
public class ConexionBD {
    
    private static Connection conexion = null;
    private static ResultSet resultado;
    private static ResultSetMetaData meta;
    
    //Constructor por defecto
    
    public void abrirConexion( String bd, String usu, String pass ) {
        
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/" + bd;
            conexion = DriverManager.getConnection(url, usu, pass);
        }
        catch( ClassNotFoundException | SQLException sqlErr){}
    }
    
    public void realizarConsulta( String consulta ) throws Exception {
        Statement sentencia = null;
        
        try
        {
            sentencia = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                    ResultSet.CONCUR_UPDATABLE);
            resultado = sentencia.executeQuery(consulta);
            meta = resultado.getMetaData();
        }
        catch( SQLException e ){
            
            throw new Exception(e.getMessage());
        }
    }
    
    public ResultSet getResultado(){
        return resultado;
    }
    
    
     public String[] getNombreDeColumnas() {
        String[] cabecera = null;
        try {

            int numberOfColumns = meta.getColumnCount();
            cabecera = new String[numberOfColumns];

            for (int i = 1; i <= numberOfColumns; i++) {
                cabecera[i - 1] = meta.getColumnName(i);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error en la adquisición de datos");

        }
        return cabecera;
    }
    
   public Object[][] getDatos() {
        Object[][] datos = null;
        try {

            int numberOfColumns = meta.getColumnCount();
            int numberOfRows;

            resultado.last();
            numberOfRows = resultado.getRow();

            datos = new Object[numberOfRows][numberOfColumns];

            resultado.beforeFirst();
            int nFila = 1;
            while (resultado.next()) {
                for (int i = 1; i <= numberOfColumns; i++) 
                    datos[nFila - 1][i - 1] = resultado.getObject(i);
                nFila++;
              
            }
            return datos;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error en la adquisición de datos "+ex.getMessage());
            return datos;
        }

    }
    
    //insertar persona en la BD (Esto es para cuando habia dialogo (Ventana))
    public void insertarPersonaBD ( String nombre, LocalDate fec, double altura, double peso ) {
        try{
            resultado.moveToInsertRow();
            resultado.updateString(2,nombre);
            resultado.updateDouble(3, altura);
            resultado.updateDouble(4,peso);
            resultado.updateDate(5, Date.valueOf(fec));
            resultado.insertRow();
            
        }catch ( SQLException err ){
            //throw new Exception(err.getMessage());
        }
    }
    
    public void insertarPersonaBD () {
        try{
            resultado.moveToInsertRow();
            resultado.insertRow();
            
        }catch ( SQLException err ){
            //throw new Exception(err.getMessage());
        }
    }
    
    //Eliminar persona de la BD
    public void eliminarPersonaBD() throws Exception {
        try{
            resultado.deleteRow();
        }
        catch ( SQLException err ){
            throw new Exception(err.getMessage());
        }
    }
    
    //Actualizar campos en una persona de la BD
    public void actualizarInformacionBD( int idPersona, int columnIndex, Object valor )
                                        throws Exception {
        
        PreparedStatement realizador = null;
        String query = "select * from personas where id = ?";
        try
        {
            /*
                Si en el objeto encargado re la realizacion de la consulta
                indicamos el tipo de conexion con la BD el propio objeto
                ResultSet tendra permitida la actualizacion de los elementos
                de la BD
            */
            realizador = conexion.prepareStatement( query, 
                                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                    ResultSet.CONCUR_UPDATABLE);
            
            realizador.setInt( 1, idPersona);
            ResultSet filaConsulta = realizador.executeQuery();
            
            //Actualizamos el valor de la columna modificada
            filaConsulta.next();
            filaConsulta.updateObject( columnIndex, valor);
            filaConsulta.updateRow();
        }
        catch( SQLException err){
        
            throw new Exception(err.getMessage());
        }
        finally
        {
            try
            {
                realizador.close();
            }
            catch( SQLException err){}
        }
    }
    
    public void cerrarConexion(){
        
        if ( conexion != null ) {
            
            try
            {
                conexion.close();
            }
            catch( SQLException e){}
        }
    }
}
