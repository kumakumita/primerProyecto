/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtablepersonasimcbdr;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author Fernando
 */
public class Ventana extends JFrame {
    
    private PanelContenedor contenedor;
    private ConexionBD conexion;
    
    public Ventana() {
        
        conexion = new ConexionBD();
        contenedor = new PanelContenedor(conexion);
        
        this.addWindowListener( new WindowAdapter(){
            
            @Override
            public void windowOpened(WindowEvent evt){
                
               // conexion.abrirConexion( "ejerciciopersonasimc", "root", "");
                conexion.abrirConexion( "prueba", "pepe", "pepa");
                contenedor.actualizarGUITablaPersonas();
            }
            
            @Override
            public void windowClosing(WindowEvent evt){
                conexion.cerrarConexion();
                
            }
        });
        
        this.setContentPane(contenedor);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Personas-IMC");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setSize(500,600);
        this.setVisible(true);
    }
    
}
