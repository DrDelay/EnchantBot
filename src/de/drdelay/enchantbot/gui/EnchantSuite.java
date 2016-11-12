package de.drdelay.enchantbot.gui;

import de.drdelay.enchantbot.tool.EnchantBot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EnchantSuite extends Frame implements ShowStatusAble, ActionListener, KeyListener {
    private final MenuItem mclose;
    private final MenuItem mload;
    private final MenuItem msave;
    private final Button bstart;
    private final Label status;
    private final TextArea log;
    private final TextField numbW;
    private final TextField baseD;
    private final TextField factD;
    private final TextField rndD;
    private final TextField inviS;
    private final TextField wishEnch;
    private final TextField[] c;
    private final TextField[] p;
    private final TextField[] a;
    private final TextField pre10c;
    private final TextField pre10p;
    private final TextField pre10a;
    private final TextField post10c;
    private final TextField post10p;
    private final TextField post10a;
    private final Button applyPre;
    private final Button applyPost;
    private final CheckboxMenuItem mdebug;
    private boolean saved = true;
    private Button csave;
    private Button cdiscard;
    private Dialog confirm = null;

    public static void main(String[] args) {
        @SuppressWarnings({"unused", "UnusedAssignment"}) EnchantSuite unique = new EnchantSuite();
    }

    private EnchantSuite() {
        super("EnchantSuite");
        Dimension d = new Dimension(300, 675);
        setPreferredSize(d);
        setSize(d);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                EnchantSuite.this.quit();
            }
        });
        setLocation(100, 100);
        MenuBar mb = new MenuBar();
        setMenuBar(mb);
        Menu mfile = new Menu("File");
        mb.add(mfile);
        mdebug = new CheckboxMenuItem("Debug mode", false);
        mfile.add(mdebug);
        this.mload = new MenuItem("Load config");
        this.msave = new MenuItem("Save config as");
        this.mload.addActionListener(this);
        this.msave.addActionListener(this);
        mfile.add(this.mload);
        mfile.add(this.msave);
        this.mclose = new MenuItem("Close");
        mfile.add(this.mclose);
        this.mclose.addActionListener(this);
        this.bstart = new Button("Start");
        add(this.bstart, "Center");
        this.bstart.addActionListener(this);
        Panel bot = new Panel(new BorderLayout());
        add(bot, "South");
        this.status = new Label();
        bot.add(this.status, "Center");
        this.status.setEnabled(false);
        this.log = new TextArea(5, 1);
        this.log.setEditable(false);
        bot.add(this.log, "South");
        Panel top = new Panel(new GridLayout(21, 4));
        add(top, "North");
        this.baseD = new TextField("45");
        this.factD = new TextField("375");
        this.rndD = new TextField("10");
        this.numbW = new TextField("1");
        this.inviS = new TextField("59");
        this.wishEnch = new TextField("16");
        baseD.addKeyListener(this);
        factD.addKeyListener(this);
        rndD.addKeyListener(this);
        numbW.addKeyListener(this);
        inviS.addKeyListener(this);
        wishEnch.addKeyListener(this);
        top.add(new Label("baseDelay"));
        top.add(this.baseD);
        top.add(new Label("factorDelay"));
        top.add(this.factD);
        top.add(new Label("rndDelay"));
        top.add(this.rndD);
        top.add(new Label("numberW"));
        top.add(this.numbW);
        top.add(new Label("inviSize"));
        top.add(this.inviS);
        top.add(new Label("wishEnch"));
        top.add(this.wishEnch);
        top.add(new Label("E"));
        top.add(new Label("Card"));
        top.add(new Label("Prot"));
        top.add(new Label("Addit"));
        this.c = new TextField[15];
        this.p = new TextField[15];
        this.a = new TextField[15];
        for (int i = 0; i < 15; i++) {
            top.add(new Label(String.valueOf(i + 1)));
            this.c[i] = new TextField("0");
            this.p[i] = new TextField("0");
            this.a[i] = new TextField("0");
            c[i].addKeyListener(this);
            p[i].addKeyListener(this);
            a[i].addKeyListener(this);
            top.add(this.c[i]);
            top.add(this.p[i]);
            top.add(this.a[i]);
        }
        this.applyPre = new Button("All Pre10");
        this.applyPost = new Button("All Hyper");
        this.applyPre.addActionListener(this);
        this.applyPost.addActionListener(this);
        this.pre10c = new TextField();
        this.pre10p = new TextField();
        this.pre10a = new TextField();
        this.post10c = new TextField();
        this.post10p = new TextField();
        this.post10a = new TextField();
        top.add(this.applyPre);
        top.add(this.pre10c);
        top.add(this.pre10p);
        top.add(this.pre10a);
        top.add(this.applyPost);
        top.add(this.post10c);
        top.add(this.post10p);
        top.add(this.post10a);
        setVisible(true);
    }

    private void showDiscardConfirm() {
        confirm = new Dialog(this, "Discard changes", true);
        confirm.setFocusable(true);
        confirm.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    confirm.dispose();
                    confirm = null;
                }
            }
        });
        confirm.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirm.dispose();
                confirm = null;
            }
        });
        confirm.add(new Label("There are unsaved changes", Label.CENTER), BorderLayout.CENTER);
        Panel cp = new Panel(new FlowLayout());
        confirm.add(cp, BorderLayout.SOUTH);
        csave = new Button("Save config as");
        cdiscard = new Button("Discard");
        cp.add(csave);
        cp.add(cdiscard);
        cdiscard.addActionListener(this);
        csave.addActionListener(this);
        confirm.pack();
        confirm.setResizable(false);
        confirm.setLocation(this.getX() + 10, this.getY() + 50);
        confirm.setVisible(true);
    }

    private void quit() {
        if (!saved) {
            showDiscardConfirm();
        }
        dispose();
        System.exit(0);
    }

    private static String getDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public void addLogLine(String line) {
        String text = this.log.getText();
        text = text.substring(0, Math.min(text.length(), 750));
        this.log.setText(getDateTime() + ": " + line + System.getProperty("line.separator") + text);
    }

    public void setStatus(String status) {
        this.status.setText(status);
    }

    private static int[] buildArr(TextField[] tfarr) {
        int[] ret = new int[15];
        for (int i = 0; i < 15; i++) {
            String t = tfarr[i].getText().trim();
            if (t.length() == 0) {
                ret[i] = 0;
            } else {
                ret[i] = Integer.parseInt(t);
            }
        }
        return ret;
    }

    private static String getFields(TextField[] tfarr) {
        StringBuilder sb = new StringBuilder();
        for (TextField tf : tfarr) {
            sb.append(tf.getText().trim());
            sb.append("\r\n");
        }
        return sb.toString();
    }

    @SuppressWarnings("ConstantConditions")
    private void load() {
        FileDialog fd = new FileDialog(this, "Load config", FileDialog.LOAD);
        fd.setDirectory(".");
        fd.setLocation(this.getX() + 10, this.getY() + 100);
        fd.setVisible(true);
        String filename = fd.getFile();
        if (filename == null) {
            new Warning(this, "No file selected, nothing loaded");
        } else {
            File f = new File(fd.getDirectory() + filename);
            if (f.exists() && !f.isDirectory()) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    ArrayList<String> loadlines = new ArrayList<>();
                    for (String line; (line = br.readLine()) != null; ) {
                        loadlines.add(line.trim());
                    }
                    br.close();
                    if (loadlines.size() != 51) {
                        new Warning(this, "Load file invalid");
                        return;
                    }
                    if (!saved) {
                        showDiscardConfirm();
                    }
                    saved = true;
                    int i = 0;
                    for (String s : loadlines) {
                        if (i == 0) {
                            baseD.setText(s);
                        } else if (i == 1) {
                            factD.setText(s);
                        } else if (i == 2) {
                            rndD.setText(s);
                        } else if (i == 3) {
                            numbW.setText(s);
                        } else if (i == 4) {
                            inviS.setText(s);
                        } else if (i == 5) {
                            wishEnch.setText(s);
                        } else if (i <= 20) {
                            c[i - 6].setText(s);
                        } else if (i <= 35) {
                            p[i - 21].setText(s);
                        } else {
                            a[i - 36].setText(s);
                        }
                        i++;
                    }
                } catch (IOException e) {
                    new Warning(this, "Load failed: " + e.getMessage());
                }
            } else {
                new Warning(this, "This file does not exist");
            }
        }
    }

    private void save() {
        FileDialog fd = new FileDialog(this, "Save config as", FileDialog.SAVE);
        fd.setDirectory(".");
        fd.setLocation(this.getX() + 10, this.getY() + 100);
        fd.setVisible(true);
        String filename = fd.getFile();
        if (filename == null) {
            new Warning(this, "No file selected, nothing saved");
        } else {
            File file = new File(fd.getDirectory() + filename);
            if (!file.exists()) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                } catch (IOException ex) {
                    new Warning(this, "Error while creating file: " + ex.getMessage());
                    return;
                }
            }
            try (FileOutputStream fop = new FileOutputStream(file)) {
                String output = baseD.getText().trim() + "\r\n" + factD.getText().trim() + "\r\n" + rndD.getText().trim() + "\r\n" + numbW.getText().trim() + "\r\n" + inviS.getText().trim() + "\r\n" + wishEnch.getText().trim() + "\r\n";
                output += getFields(c) + getFields(p) + getFields(a);
                byte[] contentInBytes = output.getBytes();
                fop.write(contentInBytes);
                fop.flush();
                fop.close();
            } catch (IOException ex) {
                new Warning(this, "Error while saving: " + ex.getMessage());
                return;
            }
            saved = true;
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src.equals(this.mclose)) {
            quit();
        } else if (src.equals(this.bstart)) {
            this.bstart.setEnabled(false);
            bstart.setLabel("Running - leave lab to stop");
            setStatus("Start Button pressed");
            EnchantBot botInst = null;
            try {
                setStatus("Creating bot instance ..");
                botInst = new EnchantBot(this, Integer.parseInt(this.baseD.getText().trim()), Integer.parseInt(this.factD.getText().trim()), Integer.parseInt(this.rndD.getText().trim()), Integer.parseInt(this.numbW.getText().trim()), Integer.parseInt(this.inviS.getText().trim()), Integer.parseInt(this.wishEnch.getText().trim()), buildArr(c), buildArr(p), buildArr(a), mdebug.getState());
                setStatus("Bot instance created");
            } catch (NumberFormatException ex) {
                new Warning(this, "All entries must be Integers");
            } catch (Exception ex) {
                new Warning(this, ex.getMessage());
            }
            if (botInst != null) {
                setStatus("Bot loop starting");
                try {
                    botInst.run();
                } catch (Exception ex) {
                    new Warning(this, getDateTime() + ": " + ex.getMessage());
                }
            }
            setStatus("Bot Instance terminated");
            bstart.setLabel("Start");
            this.bstart.setEnabled(true);
        } else if (src.equals(this.mload)) {
            load();
        } else if (src.equals(this.msave)) {
            save();
        } else if (src.equals(this.applyPre)) {
            String cA = this.pre10c.getText();
            String pA = this.pre10p.getText();
            String aA = this.pre10a.getText();
            for (int i = 0; i < 9; i++) {
                this.c[i].setText(cA);
                this.p[i].setText(pA);
                this.a[i].setText(aA);
            }
        } else if (src.equals(this.applyPost)) {
            String cA = this.post10c.getText();
            String pA = this.post10p.getText();
            String aA = this.post10a.getText();
            for (int i = 9; i < 15; i++) {
                this.c[i].setText(cA);
                this.p[i].setText(pA);
                this.a[i].setText(aA);
            }
        } else if (src.equals(csave)) {
            save();
            confirm.requestFocusInWindow();
            confirm.requestFocus();
            if (saved) {
                confirm.dispose();
                confirm = null;
            }
        } else if (src.equals(cdiscard)) {
            confirm.dispose();
            confirm = null;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        saved = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
