package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

import com.toedter.calendar.JDateChooser;

public class TicketManagementSystem extends JFrame {
    private final JTextField ticketIdField;
    private final JComboBox<String> regionComboBox;
    private final JFormattedTextField quantityField;
    private final JFormattedTextField priceField;
    private final JDateChooser issueDateChooser;
    private final JDateChooser rewardDateChooser;
    private final JTable ticketTable;
    private final DefaultTableModel tableModel;
    
    private final ArrayList<TicketData> ticketList = new ArrayList<>();

    private static class TicketData {
        String id;
        String region;
        int quantity;
        double price;
        Date issueDate;
        Date rewardDate;

        public TicketData(String id, String region, int quantity, double price, 
                         Date issueDate, Date rewardDate) {
            this.id = id;
            this.region = region;
            this.quantity = quantity;
            this.price = price;
            this.issueDate = issueDate;
            this.rewardDate = rewardDate;
        }
    }

    public TicketManagementSystem() {
        setTitle("Quản lý Doanh Thu Vé Số");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 215), 1),
                "Thông tin vé số",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(0, 120, 215)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 12);
        
        ticketIdField = createStyledTextField();
        addFormRow(inputPanel, "Mã Vé Số:", ticketIdField, gbc, 0, labelFont);

        String[] regions = {"Miền Bắc", "Miền Trung", "Miền Nam"};
        regionComboBox = new JComboBox<>(regions);
        styleComboBox(regionComboBox);
        addFormRow(inputPanel, "Vùng Miền:", regionComboBox, gbc, 1, labelFont);

        // Tạo NumberFormatter cho số lượng
        NumberFormat quantityFormat = NumberFormat.getIntegerInstance();
        NumberFormatter quantityFormatter = new NumberFormatter(quantityFormat) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.trim().isEmpty()) {
                    return null;
                }
                // Loại bỏ dấu phẩy và khoảng trắng
                text = text.replaceAll("[,\\s]", "");
                return super.stringToValue(text);
            }
        };
        quantityFormatter.setValueClass(Integer.class);
        quantityFormatter.setMinimum(0);
        quantityFormatter.setAllowsInvalid(false);
        quantityField = new JFormattedTextField(quantityFormatter);
        styleFormattedTextField(quantityField);
        addFormRow(inputPanel, "Số Lượng:", quantityField, gbc, 2, labelFont);

        // Tạo NumberFormatter cho giá vé
        NumberFormat priceFormat = NumberFormat.getIntegerInstance();
        NumberFormatter priceFormatter = new NumberFormatter(priceFormat) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.trim().isEmpty()) {
                    return null;
                }
                // Loại bỏ dấu phẩy và khoảng trắng
                text = text.replaceAll("[,\\s]", "");
                return super.stringToValue(text);
            }
        };
        priceFormatter.setValueClass(Integer.class);
        priceFormatter.setMinimum(0);
        priceFormatter.setAllowsInvalid(false);
        priceField = new JFormattedTextField(priceFormatter);
        styleFormattedTextField(priceField);
        addFormRow(inputPanel, "Giá Vé:", priceField, gbc, 3, labelFont);

        issueDateChooser = new JDateChooser();
        styleDateChooser(issueDateChooser);
        addFormRow(inputPanel, "Ngày Phát Hành:", issueDateChooser, gbc, 4, labelFont);

        rewardDateChooser = new JDateChooser();
        styleDateChooser(rewardDateChooser);
        addFormRow(inputPanel, "Ngày Nhận Thưởng:", rewardDateChooser, gbc, 5, labelFont);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton addButton = createStyledButton("Thêm", new Color(40, 167, 69), Color.WHITE);
        JButton editButton = createStyledButton("Sửa", new Color(0, 123, 255), Color.WHITE);
        JButton deleteButton = createStyledButton("Xoá", new Color(220, 53, 69), Color.WHITE);
        JButton searchButton = createStyledButton("Tìm Kiếm", new Color(108, 117, 125), Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(searchButton);

        String[] columnNames = {"Mã Vé", "Vùng Miền", "Số Lượng", "Giá Vé", 
                              "Ngày Phát Hành", "Ngày Nhận Thưởng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ticketTable = new JTable(tableModel);
        styleTable(ticketTable);
        
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setPreferredSize(new Dimension(750, 200));

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);

        addButton.addActionListener(e -> addTicket());
        editButton.addActionListener(e -> editTicket());
        deleteButton.addActionListener(e -> deleteTicket());
        searchButton.addActionListener(e -> searchTicket());

        pack();
        setLocationRelativeTo(null);
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setGridColor(new Color(233, 236, 239));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(13, 110, 253, 25));
        table.setSelectionForeground(Color.BLACK);
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setBackground(Color.WHITE);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void styleDateChooser(JDateChooser dateChooser) {
        dateChooser.setPreferredSize(new Dimension(200, 30));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void styleFormattedTextField(JFormattedTextField field) {
        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setColumns(10);
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.selectAll();
            }
        });
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                String text = field.getText();
                if (text != null && !text.trim().isEmpty()) {
                    field.setText(text.trim());
                }
            }
        });
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 35));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(makeDarker(bgColor, 0.9f));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private Color makeDarker(Color color, float factor) {
        return new Color(
            Math.max((int)(color.getRed() * factor), 0),
            Math.max((int)(color.getGreen() * factor), 0),
            Math.max((int)(color.getBlue() * factor), 0),
            color.getAlpha()
        );
    }
    
    private void addFormRow(JPanel panel, String labelText, JComponent component, 
                          GridBagConstraints gbc, int row, Font labelFont) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9;
        panel.add(component, gbc);
    }

    private void addTicket() {
        if (validateInputs()) {
            TicketData newTicket = new TicketData(
                ticketIdField.getText(),
                (String) regionComboBox.getSelectedItem(),
                ((Number)quantityField.getValue()).intValue(),
                ((Number)priceField.getValue()).doubleValue(),
                issueDateChooser.getDate(),
                rewardDateChooser.getDate()
            );
            
            ticketList.add(newTicket);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
            String[] rowData = {
                newTicket.id,
                newTicket.region,
                String.valueOf(newTicket.quantity),
                currencyFormat.format(newTicket.price),
                dateFormat.format(newTicket.issueDate),
                dateFormat.format(newTicket.rewardDate)
            };
            tableModel.addRow(rowData);
            
            clearFields();
            JOptionPane.showMessageDialog(this, 
                "Thêm vé số thành công!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editTicket() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow >= 0 && validateInputs()) {
            TicketData updatedTicket = new TicketData(
                ticketIdField.getText(),
                (String) regionComboBox.getSelectedItem(),
                ((Number)quantityField.getValue()).intValue(),
                ((Number)priceField.getValue()).doubleValue(),
                issueDateChooser.getDate(),
                rewardDateChooser.getDate()
            );
            
            ticketList.set(selectedRow, updatedTicket);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
            tableModel.setValueAt(updatedTicket.id, selectedRow, 0);
            tableModel.setValueAt(updatedTicket.region, selectedRow, 1);
            tableModel.setValueAt(String.valueOf(updatedTicket.quantity), selectedRow, 2);
            tableModel.setValueAt(currencyFormat.format(updatedTicket.price), selectedRow, 3);
            tableModel.setValueAt(dateFormat.format(updatedTicket.issueDate), selectedRow, 4);
            tableModel.setValueAt(dateFormat.format(updatedTicket.rewardDate), selectedRow, 5);
            
            clearFields();
            JOptionPane.showMessageDialog(this, 
                "Cập nhật vé số thành công!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        } else if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn vé số cần sửa!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteTicket() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa vé số này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                ticketList.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                clearFields();
                JOptionPane.showMessageDialog(this,
                    "Xóa vé số thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn vé số cần xóa!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void searchTicket() {
        String searchId = ticketIdField.getText().trim();
        if (searchId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập mã vé số cần tìm!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).toString().equals(searchId)) {
                ticketTable.setRowSelectionInterval(i, i);
                ticketTable.scrollRectToVisible(ticketTable.getCellRect(i, 0, true));
                found = true;
                
                loadTicketDataToForm(i);
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy vé số với mã " + searchId,
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadTicketDataToForm(int row) {
        TicketData ticket = ticketList.get(row);
        ticketIdField.setText(ticket.id);
        regionComboBox.setSelectedItem(ticket.region);
        quantityField.setValue(ticket.quantity);
        priceField.setValue(ticket.price);
        issueDateChooser.setDate(ticket.issueDate);
        rewardDateChooser.setDate(ticket.rewardDate);
    }

    private boolean validateInputs() {
        if (ticketIdField.getText().trim().isEmpty()) {
            showError("Vui lòng nhập mã vé số!");
            ticketIdField.requestFocus();
            return false;
        }

        if (quantityField.getValue() == null) {
            showError("Vui lòng nhập số lượng hợp lệ!");
            quantityField.requestFocus();
            return false;
        }

        if (priceField.getValue() == null) {
            showError("Vui lòng nhập giá vé hợp lệ!");
            priceField.requestFocus();
            return false;
        }

        if (issueDateChooser.getDate() == null) {
            showError("Vui lòng chọn ngày phát hành!");
            issueDateChooser.requestFocus();
            return false;
        }

        if (rewardDateChooser.getDate() == null) {
            showError("Vui lòng chọn ngày nhận thưởng!");
            rewardDateChooser.requestFocus();
            return false;
        }

        if (rewardDateChooser.getDate().before(issueDateChooser.getDate())) {
            showError("Ngày nhận thưởng phải sau ngày phát hành!");
            rewardDateChooser.requestFocus();
            return false;
        }

        String ticketId = ticketIdField.getText().trim();
        int selectedRow = ticketTable.getSelectedRow();
        for (int i = 0; i < ticketList.size(); i++) {
            if (i != selectedRow && ticketList.get(i).id.equals(ticketId)) {
                showError("Mã vé số đã tồn tại!");
                ticketIdField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Lỗi",
            JOptionPane.ERROR_MESSAGE);
    }

    private void clearFields() {
        ticketIdField.setText("");
        regionComboBox.setSelectedIndex(0);
        quantityField.setValue(null);
        priceField.setValue(null);
        issueDateChooser.setDate(null);
        rewardDateChooser.setDate(null);
        ticketTable.clearSelection();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            TicketManagementSystem system = new TicketManagementSystem();
            system.setVisible(true);
        });
    }
}