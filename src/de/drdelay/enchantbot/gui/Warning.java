package de.drdelay.enchantbot.gui;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Warning extends Dialog implements ActionListener {
    public Warning(Window owner, String warnung) {
        super(owner, "Warning");
        this.setModal(true);
        Toolkit.getDefaultToolkit().beep();
        this.setLayout(new GridLayout(2, 1, 2, 2));
        if (warnung == null || warnung.isEmpty()) {
            this.add(new Label("No message"));
        } else {
            this.add(new Label(warnung));
        }
        Button close = new Button("Close");
        add(close);
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int kp = e.getKeyCode();
                if (kp == KeyEvent.VK_ENTER || kp == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        close.addActionListener(this);
        pack();
        this.setResizable(false);
        this.setLocation(owner.getX() + 10, owner.getY() + 100);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String click = e.getActionCommand();
        if (click.equals("Close")) {
            dispose();
        }
    }
}
