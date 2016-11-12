package de.drdelay.enchantbot.tool;

import de.drdelay.enchantbot.gui.ShowStatusAble;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class EnchantBot {
    private ShowStatusAble frame;
    private Robot robot = null;
    private int baseDelay;
    private Point factor;
    private int factorDelay;
    private int rndDelay;
    private Rectangle screenSize;
    private int numberW;
    private int currentW;
    private int wishEnch;
    private Point searchEye;
    private int currentEnch;
    private int totalEnchants = 0;
    private int weaponEnchants = 0;
    private Point[] cards;
    private Point[] prots;
    private Point[] additional;
    private Point item;
    private Point itemIn;
    private BufferedImage enchant;
    private BufferedImage[] enchPics;
    private final boolean debug;

    public EnchantBot(ShowStatusAble frame, int baseDelay, int factorDelay, int rndDelay, int numberW, int inviSize, int wishEnch, int[] cards, int[] prots, int[] additional, boolean debug) throws EnchantBotException {
        if (frame == null) {
            throw new EnchantBotException("Invalid frame");
        }
        frame.setStatus("Initializing");
        this.frame = frame;
        if (baseDelay < 45) {
            throw new EnchantBotException("BaseDelay must be at least 45");
        }
        this.baseDelay = baseDelay;
        if (factorDelay < 150) {
            throw new EnchantBotException("factorDelay must be at least 150");
        }
        this.factorDelay = factorDelay;
        if (rndDelay < 10) {
            throw new EnchantBotException("rndDelay must be at least 10");
        }
        this.rndDelay = rndDelay;
        if (numberW < 1) {
            throw new EnchantBotException("numberW must be at least 1");
        }
        this.numberW = numberW;
        if (wishEnch < 2) {
            throw new EnchantBotException("wishEnch must be at least 2");
        }
        if (inviSize < (numberW + 2) || inviSize > 59) {
            throw new EnchantBotException("inviSize must be at least numberW + 2, and at max 59");
        }
        this.wishEnch = wishEnch;
        enchant = readFile("enchant.png");
        enchPics = new BufferedImage[16];
        for (int i = 0; i < 16; i++) {
            enchPics[i] = readFile("e" + (i + 1) + ".png");
        }
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new EnchantBotException("Failed to create Robot: " + e.getMessage());
        }
        frame.addLogLine("Robot#" + robot.hashCode() + " created, focus the gamewindow");
        robot.delay(4000);
        screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        searchEye = subImgFromFile("../searcheye.png", screenShot());
        item = getPointByPos(inviSize);
        factor = new Point(searchEye.x + 631, searchEye.y - 29);
        if (cards.length != 15) {
            throw new EnchantBotException("Cards array not 15 entries long");
        }
        this.cards = new Point[15];
        for (int i = 0; i < 15; i++) {
            int card = cards[i];
            if ((card < 2 && card != 0) || card >= inviSize) {
                throw new EnchantBotException("Card " + (i + 1) + " not between 2 and inviSize - 1, or 0");
            }
            this.cards[i] = getPointByPos(card);
        }
        if (prots.length != 15) {
            throw new EnchantBotException("Prots array not 15 entries long");
        }
        this.prots = new Point[15];
        for (int i = 0; i < 15; i++) {
            int prot = prots[i];
            if ((prot < 2 && prot != 0) || prot >= inviSize) {
                throw new EnchantBotException("Prot " + (i + 1) + " not between 2 and inviSize - 1, or 0");
            }
            this.prots[i] = getPointByPos(prot);
        }
        if (additional.length != 15) {
            throw new EnchantBotException("Additional array not 15 entries long");
        }
        this.additional = new Point[15];
        for (int i = 0; i < 15; i++) {
            int addit = additional[i];
            if ((addit < 2 && addit != 0) || addit >= inviSize) {
                throw new EnchantBotException("Addit " + (i + 1) + " not between 2 and inviSize - 1, or 0");
            }
            this.additional[i] = getPointByPos(addit);
        }
        itemIn = getPointByPos(inviSize - numberW + 1);
        this.searchEye = null;
        this.debug = debug;
        frame.addLogLine("Init successful");
        frame.setStatus("Ready");
    }

    private void saveBuffered(BufferedImage img, String filename) throws IOException {
        File outputfile = new File(filename + ".png");
        ImageIO.write(img, "png", outputfile);
    }

    public void run() throws EnchantBotException {
        frame.setStatus("Running");
        for (currentW = 1; currentW <= numberW; currentW++) {
            boolean fresh = true;
            frame.addLogLine("Starting item " + currentW + " of " + (numberW));
            while (currentEnch < wishEnch) {
                if (fresh) {
                    robot.mouseMove(itemIn.x, itemIn.y);
                } else {
                    robot.mouseMove(item.x, item.y);
                }
                robot.delay(baseDelay);
                checkEnchant();
                doEnchant();
                fresh = false;
                robot.delay(baseDelay + randInt(rndDelay));
            }
            frame.addLogLine("Finish after " + weaponEnchants + " Enchants");
            weaponEnchants = 0;
            currentEnch = -1;
        }
        frame.addLogLine("Finish all after " + totalEnchants + " enchants!");
    }

    private void doEnchant() throws EnchantBotException {
        if (currentEnch >= wishEnch) {
            if (weaponEnchants == 0) {
                doubleClick();
                robot.delay(baseDelay + randInt(rndDelay));
                factor();
            }
            return;
        }
        int cem1 = currentEnch - 1;
        if (cards[currentEnch - 1] == null) {
            throw new EnchantBotException("Undefined card for e" + currentEnch);
        }
        frame.setStatus("Weapon " + currentW + "/" + numberW + " e" + currentEnch + " after " + weaponEnchants + " trys (total: " + totalEnchants + ")");
        doubleClick();
        robot.delay(baseDelay + randInt(rndDelay));
        itemIn(cards[cem1]);
        if (prots[cem1] != null) {
            itemIn(prots[cem1]);
        }
        if (additional[cem1] != null) {
            itemIn(additional[cem1]);
        }
        totalEnchants++;
        weaponEnchants++;
        factor();
    }

    private void checkEnchant() throws EnchantBotException {
        BufferedImage itemEnch = findEnchant();
        for (int i = 15; i >= 0; i--) {
            if (subImg(enchPics[i], itemEnch) != null) {
                currentEnch = i + 1;
                return;
            }
        }
        throw new EnchantBotException("Weapon enchant not known");
    }

    private BufferedImage findEnchant() throws EnchantBotException {
        BufferedImage screen = screenShot();
        int xoff = Math.max(itemIn.x - 40, 0);
        int xto = Math.min(screen.getWidth() - xoff - 1, 500);
        screen = screen.getSubimage(xoff, 0, xto, screen.getHeight());
        Point enPos = subImg(enchant, screen);
        if (enPos == null) {
            frame.addLogLine("Could not find enchant, trying to factor again");
            robot.delay(1000);
            factor();
            robot.delay(baseDelay);
            robot.mouseMove(item.x, item.y);
            robot.delay(2 * baseDelay);
            screen = screenShot();
            screen = screen.getSubimage(xoff, 0, xto, screen.getHeight());
            enPos = subImg(enchant, screen);
            if (enPos == null) {
                if (debug) {
                    try {
                        saveBuffered(screen, new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()));
                    } catch (IOException e) {
                        throw new EnchantBotException("Could not find enchant, also failed to save the debug image: " + e.getMessage());
                    }
                    throw new EnchantBotException("Could not find enchant. Saved debug image as current_timestamp.png");
                }
                throw new EnchantBotException("Could not find enchant");
            }
        }
        return screen.getSubimage(enPos.x - 33, enPos.y - 5, 66, 13);
    }

    private void click() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(baseDelay);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private void doubleClick() {
        click();
        robot.delay(baseDelay);
        click();
    }

    private static Point subImgFromFile(String needleFile, BufferedImage haystack) throws EnchantBotException {
        Point ret = subImg(readFile(needleFile), haystack);
        if (ret == null) {
            throw new EnchantBotException("Failed to find image '" + needleFile + "' on screen");
        }
        return ret;
    }

    private static Point subImg(BufferedImage needle, BufferedImage haystack) {
        int hayW = haystack.getWidth() - needle.getWidth();
        int hayH = haystack.getHeight() - needle.getHeight();
        for (int hIx = 0; hIx < hayW; hIx++) {
            for (int hIy = 0; hIy < hayH; hIy++) {
                if (compareImg(needle, haystack, hIx, hIy)) {
                    int toMidX = 13;
                    int toMidY = 3;
                    return new Point(hIx + toMidX, hIy + toMidY);
                }
            }
        }
        return null;
    }

    private static BufferedImage readFile(String name) throws EnchantBotException {
        try {
            return ImageIO.read(new File("res/" + name));
        } catch (IOException e) {
            throw new EnchantBotException("Failed to open file " + name + ": " + e.getMessage());
        }
    }

    private static boolean compareImg(BufferedImage needle, BufferedImage haystack, int hayOffX, int hayOffY) {
        int needW = needle.getWidth() - 1;
        int needH = needle.getHeight() - 1;
        for (int nIx = 0; nIx < needW; nIx++) {
            for (int nIy = 0; nIy < needH; nIy++) {
                int needleRGB = needle.getRGB(nIx, nIy);
                int haystackRGB = haystack.getRGB(hayOffX + nIx, hayOffY + nIy);
                if (needleRGB != haystackRGB && !isTransp(needleRGB)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void itemIn(Point pos) {
        robot.mouseMove(pos.x, pos.y);
        robot.delay(baseDelay + randInt(rndDelay));
        doubleClick();
    }

    private BufferedImage screenShot() {
        return robot.createScreenCapture(screenSize);
    }

    private void factor() {
        robot.mouseMove(factor.x, factor.y);
        click();
        robot.delay(factorDelay + randInt(rndDelay));
        click();
    }

    private static boolean isTransp(int pixel) {
        return pixel == -16777216;
    }

    private static int randInt(int max) {
        return ThreadLocalRandom.current().nextInt(0, max);
    }

    private Point getPointByPos(int pos) {
        if (pos == 0) {
            return null;
        }
        return new Point(searchEye.x + (((pos - 1)) % 10) * 33, searchEye.y + (((pos - 1)) / 10) * 33);
    }
}
