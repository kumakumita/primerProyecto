/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtablepersonasimcbdr;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author prg
 */
public class MiModelo extends DefaultTableModel{
     boolean[] columnEditable = {false, true, true, true, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {

                return columnEditable[columnIndex];
            }
    
}
