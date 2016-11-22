/* 
 * The MIT License
 *
 * Copyright 2014 David Jarski.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.davidjarski.javatzee.dice;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import com.davidjarski.javatzee.IO.Resources;

public class JGraphicalDie extends JLabel implements MouseListener, Die
{

    /**
     * The position of the first colored lock in the resource image
     */
    private static final int COLORED_LOCK_OFFSET = 3;
    private static final int IMAGE_COLUMNS = 6;
    private static final int IMAGE_ROWS = 6;
    private static BufferedImage image;
    private static BufferedImage lockImage;
    private static int width;
    private static int height;
    private static boolean coloredLock;
    private static DiceStyle mainStyle = DiceStyle.getDefault();
    private static DiceStyle highlightStyle = DiceStyle.getDefault();
    private static LockStyle lockStyle = LockStyle.getDefault();
    private static int mainOffset = mainStyle.calculateOffset();
    private static int highlightOffset = highlightStyle.calculateOffset();
    
    private int value;
    private boolean locked;

    /**
     * @return the mainStyle
     */
    public static DiceStyle getMainStyle() {
        return mainStyle;
    }

    /**
     * @param style the mainStyle to set
     */
    public static void setMainStyle(DiceStyle style) {
        mainStyle = style;
        mainOffset = style.calculateOffset();
    }

    /**
     * @return the highlightStyle
     */
    public static DiceStyle getHighlightStyle() {
        return highlightStyle;
    }

    /**
     * @param style the highlightStyle to set
     */
    public static void setHighlightStyle(DiceStyle style) {
        highlightStyle = style;
        highlightOffset = style.calculateOffset();
    }

    /**
     * @return the lockStyle
     */
    public static LockStyle getLockStyle() {
        return lockStyle;
    }

    /**
     * @param style the lockStyle to set
     */
    public static void setLockStyle(LockStyle style) {
        lockStyle = style;
        updateLockImage();
    }

    public static boolean isColoredLock() {
        return coloredLock;
    }

    public static void setColoredLock(boolean coloredLock) {
        JGraphicalDie.coloredLock = coloredLock;
        updateLockImage();
    }

    private static void updateLockImage() {
        if (image == null || lockStyle == LockStyle.NONE) {
            lockImage = null;
        } else {
            int x = lockStyle.calculateOffset();
            int y = 0;
            lockImage = image.getSubimage(x, y, width, height);
        }
    }

    public JGraphicalDie() {
        value = (int) (Math.random() * 5) + 1; // no sixes on instantiation, for looks
        setOpaque(false);
        addMouseListener(this);
        
        if (image == null) {
            try {
                image = ImageIO.read(getClass().getResource(Resources.DICE_PATH));
                width = image.getWidth() / IMAGE_COLUMNS;
                height = image.getHeight() / IMAGE_ROWS;
                updateLockImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        Dimension dimension = new Dimension(width, height);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
        mainOffset = mainStyle.calculateOffset();
        highlightOffset = highlightStyle.calculateOffset();
    }

    @Override
    public void roll() {
        if (!locked) {
            value = (int) (Math.random() * 6) + 1;
            repaint();
        }
    }

    @Override
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        repaint();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        int x = width * (value - 1);
        int y = locked ? highlightOffset : mainOffset;
        g.drawImage(image, 0, 0, width, height, x, y, x + width, y + height, null);
        if (locked && lockStyle != LockStyle.NONE) {
            g.drawImage(lockImage, 0, 0, null);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isEnabled()) {
            locked = !locked;
            repaint();
        }
    }

    /* ************************************************************************
     Unused MouseListener methods
     *************************************************************************/
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public enum DiceStyle
    {
        WHITE(1),
        BLACK(2),
        SINGLE_HUE(3),
        SOFT_MULTI_HUE(4),
        BRIGHT_MULTI_HUE(5);

        private final int offset;
        
        public static DiceStyle create(String name) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException | NullPointerException ex) {
                return getDefault();
            }
        }

        public static DiceStyle getDefault() {
            return BRIGHT_MULTI_HUE;
        }
        
        DiceStyle(int offset) {
            this.offset = offset;
        }

        public int calculateOffset() {
            return height * offset;
        }
    }

    // Order must match the dice resource image
    public enum LockStyle
    {
        SMALL(0),
        MEDIUM(1),
        LARGE(2),
        NONE(Integer.MIN_VALUE);

        private final int offset;
        
        public static LockStyle create(String name) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException | NullPointerException ex) {
                return getDefault();
            }
        }

        public static LockStyle getDefault() {
            return MEDIUM;
        }
        
        LockStyle(int offset) {
            this.offset = offset;
        }
        
        public int calculateOffset() {
            return (coloredLock ? (width * COLORED_LOCK_OFFSET) : 0)
                    + width * offset;
        }
    }

}
