/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtablepersonasimcbdr;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;


public class PanelContenedor extends JPanel {

    private JPanel contenedorJP;
    private JPanel filtroJP;
    private JScrollPane panelJSP;
    private JTable tablaJT;
    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> ordenadorModeloTRS;
    private JComboBox filtroJCB;
    private JTextField filtroJTF;
    private JButton insertarJB;
    private JButton eliminarJB;
    private JButton calcularJB;
    private TableModelListener escuchadorTML;
    private ManejadorEventoActionJButton escuchadorAL;
    private ConexionBD conexion;

    public PanelContenedor(ConexionBD conexion) {

        this.conexion = conexion;
        initComponents();
    }

    private void initComponents() {

        //Inicializamos todas las variables
        contenedorJP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filtroJP = new JPanel(new GridLayout(0, 1, 0, 10));
        panelJSP = new JScrollPane();
        tablaJT = new JTable();
        modelo = new MiModelo();
 
      
        filtroJTF = new JTextField();
        filtroJCB = new JComboBox();
        insertarJB = new JButton("Registrar");
        eliminarJB = new JButton("Eliminar");
        calcularJB = new JButton("Calcular IMC");
        escuchadorTML = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {

                if (e.getType() == TableModelEvent.UPDATE) {

                    int rowIndex = tablaJT.getSelectedRow();
                    int columnIndex = tablaJT.getSelectedColumn();
                        System.out.println("actualizando");
                    if (rowIndex != -1 && columnIndex != -1) {
                           
                        int id = (int) tablaJT.getValueAt(rowIndex, 0);
                        Object aValue = tablaJT.getValueAt(rowIndex, columnIndex);
                        System.out.println("f:"+rowIndex+" c:"+columnIndex+"  v:"+aValue.toString());
                        try {
                           
                            conexion.getResultado().absolute(rowIndex+1);
                            conexion.getResultado().updateObject(columnIndex+1, aValue);
                            conexion.getResultado().updateRow();
                            
                            ///actualizarGUITablaPersonas();
                            
                            System.out.println("rs fila:"+conexion.getResultado().getRow());
                           // conexion.actualizarInformacionBD(id, columnIndex + 1, aValue);
                        } catch (Exception err) {

                            JOptionPane.showMessageDialog(PanelContenedor.this,
                                    err.getMessage(), "Error update", JOptionPane.ERROR_MESSAGE);
                        }

                        //Limpiamos la fila seleccionada
                        tablaJT.clearSelection();
                    }
                }
            }
        };
        escuchadorAL = new ManejadorEventoActionJButton();
        conexion = new ConexionBD();

        //Manejadores JButton
        insertarJB.addActionListener(escuchadorAL);
        eliminarJB.addActionListener(escuchadorAL);
        calcularJB.addActionListener(escuchadorAL);

        //Manejador Campo de Texto
       

        //Caracteristicas Tabla
        //Manejador que nos permite mostrar un mensaje cuando la tabla se actualiza
        modelo.addTableModelListener(escuchadorTML);

        //Caracteristicas tabla
        //Metodo para indicar que solo podemos seleccionar una fila
        tablaJT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaJT.setModel(modelo);
      

        //Metodo que nos permite indicar que las columnas no se puedan desplazar
        tablaJT.getTableHeader().setReorderingAllowed(false);
        panelJSP.setViewportView(tablaJT);

        //Tamaño componentes
        filtroJP.setBounds(50, 25, 400, 50);
        filtroJP.add(filtroJCB);
        filtroJP.add(filtroJTF);
        panelJSP.setBounds(50, 100, 400, 300);
        contenedorJP.setBounds(50, 450, 400, 50);
        contenedorJP.add(insertarJB);
        contenedorJP.add(eliminarJB);
        contenedorJP.add(calcularJB);

        this.setLayout(null);
        this.add(filtroJP);
        this.add(panelJSP);
        this.add(contenedorJP);
    }

    public void actualizarGUITablaPersonas() {

        //Object[] informacion = null;
        Object[][] datos;
        String[] columnas;

        try {
            conexion.realizarConsulta("select * from personas");
            datos = conexion.getDatos();
            columnas = conexion.getNombreDeColumnas();

            //Si no se ha rellenado a informacion del JComboBox se rellena aqui
            if (filtroJCB.getSelectedItem() == null) {

                for (String col : (String[]) columnas) {

                    filtroJCB.addItem(col);
                }

                filtroJCB.setSelectedIndex(0);
            }

            modelo.setDataVector((Object[][]) datos, (String[]) columnas);
            

        } catch (Exception e) {

            JOptionPane.showMessageDialog(PanelContenedor.this, e.getMessage(),
                    "Error cargando tabla", JOptionPane.ERROR_MESSAGE);
        }

    }

    
    
    
    
    
    
    
    private class ManejadorEventoActionJButton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JButton aux = (JButton) e.getSource();
            int rowIndex = PanelContenedor.this.tablaJT.getSelectedRow();

            if (aux.equals(PanelContenedor.this.insertarJB)) {

                //Insertar
                //new DialogoInsercionPersona(PanelContenedor.this.conexion);
                conexion.insertarPersonaBD();
            } else if (rowIndex == -1) {

                JOptionPane.showMessageDialog(PanelContenedor.this,
                        "Debe de seleccionar una fila", "Error", JOptionPane.ERROR_MESSAGE);

            } else if (aux.equals(PanelContenedor.this.eliminarJB)) {

                //Eliminar
                int opcion = JOptionPane.showConfirmDialog(PanelContenedor.this,
                        "¿Desea eliminar la persona seleccionada?", "Eliminar",
                        JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {

                    //Eliminar persona BD
                    try {
                        conexion.getResultado().absolute(rowIndex+1);
                        System.out.println("Esta es la filaaaaa" + conexion.getResultado().getRow());
                        conexion.eliminarPersonaBD();
                    } catch (Exception err) {

                        JOptionPane.showMessageDialog(PanelContenedor.this,
                                err.getMessage(), "Error borrando", JOptionPane.ERROR_MESSAGE);
                    }
                } else {

                    JOptionPane.showMessageDialog(PanelContenedor.this,
                            "Persona no eliminada", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                //Consultar IMC
                //Debemos de obtener la informacion de la BD del alumno selec

                String nombre = (String) tablaJT.getValueAt(rowIndex, 1);
                LocalDate fec = ((java.sql.Date) tablaJT.getValueAt(rowIndex, 4)).toLocalDate();
                double altura = Double.parseDouble(tablaJT.getValueAt(rowIndex, 2).toString());
                double peso = Double.parseDouble(tablaJT.getValueAt(rowIndex, 3).toString());

                double imc = peso / (Math.pow(altura, 2));

                JOptionPane.showMessageDialog(PanelContenedor.this,
                        String.format("%-10s%s%-10s%.2f", "Nombre:", nombre + System.lineSeparator(), "IMC:", imc),
                        "IMC", JOptionPane.INFORMATION_MESSAGE);

                PanelContenedor.this.tablaJT.clearSelection();
            }

            //Actualizamos la informacion del GUI con la BD
            actualizarGUITablaPersonas();
        }

    }

}
