/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtablepersonasimcbdr;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Fernando
 */
public class DialogoInsercionPersona extends JDialog {
    
    private JPanel contenedorJP;
    private JLabel nombreJL;
    private JTextField nombreJTF;
    private JLabel alturaJL;
    private JFormattedTextField alturaJFTF;
    private JLabel pesoJL;
    private JFormattedTextField pesoJFTF;
    private JLabel fecJL;
    private JFormattedTextField fecJFTF;
    private JButton botonJB;
    private ConexionBD conexion;
    
    public DialogoInsercionPersona( ConexionBD conexion ) {
        
        this.conexion = conexion;
        initComponents();
        
    }
    
    private void initComponents(){
        
        //Etiquetas
        nombreJL = new JLabel("Nombre");
        alturaJL = new JLabel("Altura");
        pesoJL   = new JLabel("Peso");
        fecJL    = new JLabel("FecNac");
        
        //Verificador Campo Nombre
        InputVerifier verNom = new InputVerifier(){
            
            @Override
            public boolean verify(JComponent input) {
                
                JTextField aux = (JTextField) input;
                
                if ( aux.getText().isEmpty()){
                    
                    JOptionPane.showMessageDialog( DialogoInsercionPersona.this,
                    "El campo \"" + nombreJTF.getName() + "\" no puede estar vacio",
                    "Campo Vacio", JOptionPane.ERROR_MESSAGE);
                    
                    return false;
                }
                
                return true;
            }
            
        };
        
        //Formatos Campos de Textos numericos
        NumberFormat formatoN = NumberFormat.getNumberInstance(Locale.ENGLISH);
        NumberFormatter formateadorPeso   = new NumberFormatter(formatoN);
        NumberFormatter formateadorAltura = new NumberFormatter(formatoN);
        formateadorPeso.setMinimum(0.0);
        formateadorAltura.setMinimum(0.0);
        formateadorAltura.setMaximum(3.0);
        
        //Escuchador que nos permite mostrar mensaje de error si no se introduce un numero correcto
        FocusListener escuchadorFL = new FocusAdapter() {
            
            @Override
            public void focusLost(FocusEvent e) {
                
                JFormattedTextField aux = (JFormattedTextField)e.getSource();
                
                try
                {
                    aux.commitEdit();
                }
                catch( ParseException ex){
                    
                    JOptionPane.showMessageDialog( DialogoInsercionPersona.this,
                    ex.getMessage(), "Error focus lost", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        //Mascara Fecha
        MaskFormatter mascara = null;
        
        try
        {
            mascara = new MaskFormatter("####-##-##");
            mascara.setPlaceholderCharacter('_');
        }
        catch( ParseException ex ){}
        
        //Campos de Texto
        nombreJTF = new JTextField();
        nombreJTF.setName(nombreJL.getText());
        nombreJTF.setInputVerifier(verNom);
        
        alturaJFTF = new JFormattedTextField(formateadorAltura);
        alturaJFTF.setName(alturaJL.getText());
        alturaJFTF.setValue(0.0);
        alturaJFTF.addFocusListener(escuchadorFL);
        
        pesoJFTF = new JFormattedTextField(formateadorPeso);
        pesoJFTF.setName(pesoJL.getText());
        pesoJFTF.setValue(0.0);
        pesoJFTF.addFocusListener(escuchadorFL);
        
        fecJFTF = new JFormattedTextField(mascara);
        fecJFTF.setName(fecJL.getText());
        fecJFTF.setValue(LocalDate.now());
        
        //Contenedor
        contenedorJP = new JPanel( new GridLayout( 0, 2, 5, 5));
        contenedorJP.add(nombreJL);
        contenedorJP.add(nombreJTF);
        contenedorJP.add(fecJL);
        contenedorJP.add(fecJFTF);
        contenedorJP.add(alturaJL);
        contenedorJP.add(alturaJFTF);
        contenedorJP.add(pesoJL);
        contenedorJP.add(pesoJFTF);
        
        //Boton
        botonJB = new JButton("Añadir");
        botonJB.addActionListener( new ActionListener(){
           
            @Override
            public void actionPerformed(ActionEvent evt){
                
                String nombre = nombreJTF.getText();
                LocalDate fec = LocalDate.parse(fecJFTF.getText());
                double altura = (double)alturaJFTF.getValue();
                double peso = (double)pesoJFTF.getValue();
                
                //Insertamos la informacion en la BD
                try
                {
                    conexion.insertarPersonaBD(nombre, fec, altura, peso);
                }
                catch( Exception err ){
                    
                    JOptionPane.showMessageDialog( DialogoInsercionPersona.this,
                    err.getMessage(), "Error insercion", JOptionPane.ERROR_MESSAGE);
                }
                
                DialogoInsercionPersona.this.dispose();
                
            }
        });
        
        //Caracteristicas visuales
        contenedorJP.setBounds( 50, 30, 300, 200 );
        botonJB.setBounds( 250, 270, 100, 30);
        this.setLayout(null);
        this.add(contenedorJP);
        this.add(botonJB);
        
        //Caracteristicas dialogo
        this.setLocationRelativeTo(null);
        this.setTitle("Registrar Persona");
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setSize( 400, 350);
        this.setResizable(false);
        this.setVisible(true);
    }
}
