/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restaurantapplication;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Saranya
 */
public class RestaurantApplication extends JFrame {


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RestaurantApplication().setVisible(true);
            }
        });
    }
    private final JButton button;
    private final JTable table;
   
    private final JTextField score;
    private final DefaultTableModel tableModel = new DefaultTableModel();
    
    String Score = null;

    public RestaurantApplication() throws HeadlessException {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        score = new JTextField();
        
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        button = new JButton("Load Data");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        loadData();
                        return null;
                    }
                }.execute();
            }
        });
        
        add(score, BorderLayout.PAGE_START);
        add(button, BorderLayout.PAGE_END);
        setSize(640, 480);
    }

    private void loadData() throws SQLException {

        button.setEnabled(false);
        button.setVisible(false);
        score.setEnabled(false);
        score.setVisible(false);
        table.setVisible(true);
        String url = "jdbc:mysql://localhost:3300/veg_restaurants?zeroDateTimeBehavior=convertToNull";
        String usr = "root";
        String pwd = "sharu";
        try {   
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, usr, pwd);
            
            String query="SELECT restaurants_name FROM veg_restaurants.restaurants where restaurants_score >= ? LIMIT 10;";
            PreparedStatement p = conn.prepareStatement(query);
            int s = Integer.parseInt(score.getText());
            p.setInt(1, s);
            
            ResultSet rs = p.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();

            // Names of columns
            Vector<String> columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Data of the table
            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    vector.add(rs.getObject(i));
                }
                data.add(vector);
            }
            tableModel.setDataVector(data, columnNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        button.setEnabled(true);
    }

}
