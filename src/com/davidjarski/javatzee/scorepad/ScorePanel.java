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
package com.davidjarski.javatzee.scorepad;

import com.davidjarski.javatzee.hand.Hand;
import com.davidjarski.javatzee.history.GameRecord;
import static com.davidjarski.javatzee.scorepad.ScoreConstants.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class ScorePanel extends JPanel implements MouseListener
{    
    public static final String PROP_SCORE_ENTERED = "boxesScored";
    public static final String PROP_GAME_OVER = "gameOver";
    
    public static final int NUM_BOXES = 39;    // total number of score boxes
    /**
     * The total number of bytes needed to store the state of the panel
     * score boxes.
     */
    public static final int INT_COUNT = 5; // the number of packed ints returned by getInts()
    
    private static final int ONES_ROW = 0;
    private static final int TWOS_ROW = 1;
    private static final int THREES_ROW = 2;
    private static final int FOURS_ROW = 3;
    private static final int FIVES_ROW = 4;
    private static final int SIXES_ROW = 5;
    private static final int KIND3_ROW = 6;
    private static final int KIND4_ROW = 7;
    private static final int FULL_HOUSE_ROW = 8;
    private static final int SHORT_STRAIGHT_ROW = 9;
    private static final int LONG_STRAIGHT_ROW = 10;
    private static final int KIND5_ROW = 11;
    private static final int CHANCE_ROW = 12;
    private static final int KIND5_BONUS_ROW = 13;
    
    private static final int COUNT_SHIFT = 3;
    private static final int VALUE_SHIFT = 6;
    private static final int BONUS_SHIFT = 4;
    
    private static final int COUNT_MASK = 0x07;
    private static final int VALUE_MASK = 0x3F;
    private static final int BONUS_MASK = 0x0F;
    
    private static final int ONES_TWOS_THREES = 0;
    private static final int FOURS_FIVES_SIXES = 1;
    private static final int KIND3 = 2;
    private static final int KIND4_FULL_SHORT_LONG_KIND5 = 3;
    private static final int CHANCE_BONUS = 4;
    
    private static final int TRUE = 1;
    private static final int FALSE = 0;

    private static int COLUMNS = 3;
    private static final int COLUMN_ZERO = 0;
    private static final int COLUMN_ONE = 1;
    private static final int COLUMN_TWO = 2;
    private static final int[] COLUMN_MULTIPLIERS =
            {COLUMN1_MULTIPLIER, COLUMN2_MULTIPLIER, COLUMN3_MULTIPLIER};
    
    private static Border nameHighlightBorder;
    private static Border nameStandardBorder;
    private static Border panelHighLightBorder;
    private static Border panelStandardBorder;
   
    private final PropertyChangeSupport propertySupport;
    private Hand hand;
    private ScoreBox lastBoxScored; // used to keep track of player's last move
    private ResultBox lastKind5BonusBox;  // for use with undo method
    private boolean lastBoxWasKind5;  // for use with undo method
    private boolean active;  // set to true when this is the currently used panel
    private boolean kind5BonusEnabled;
    private boolean gameOver;  // set to true when all scoreBoxes are filled
    private boolean unDoing;  // true when we are processing an undo
    private boolean usingHighlights;
    private int kind5Count;   // number of yahtzees a player has scored
    private int boxesScored;    // number of scoreBoxes with values
    private GameRecord gameRecord;

    private ResultBox[] upperSubtotalBoxes;
    private ResultBox[] upperBonusBoxes;
    private ResultBox[] upperTotalBoxes;
    private ResultBox[] lowerTotalBoxes;
    private ResultBox[] kind5BonusBoxes;
    private ResultBox[] combinedTotalBoxes;
    private ResultBox[] multipliedTotalBoxes;
    private ResultBox[][] resultBoxes;
    private ScoreBox[][] scoreBoxes;

    /**
     * Creates new form ScorePanel
     */
    public ScorePanel()
    {
        initBorders();
        initComponents();
        initBoxArrays();
        addMouseListener(this);
        setActive(false);
        propertySupport = new PropertyChangeSupport(this);
        setPlayerName("");
        reset();
        setEnabled(false);
    }

    public ScorePanel(Hand hand)
    {
        this();
        this.hand = hand;
    }
    
    public void setScoreBox(ScoreBox box)
    {
        /* Return immediately if this box has already been scored or if it isn't
           this player's turn      
        */
        if (box.getText() != null || !isActive()) {
            return;
        }
        lastBoxWasKind5 = false;
        int score = getScoreOfHand(box.getHandType());
        box.setScore(score);  // sets score to zero unless value was changed in above switch
        incrementBoxesScored();
        lastBoxScored = box;
        setEnabled(false);
        if (kind5Count >= 3 && hand.isFiveOfAKind()
                    && score > 0) {
            if (kind5BonusEnabled) {
                processKind5Bonus();
            } else {
                kind5BonusEnabled = true;
            }         
        }

        updateColumnOfLastBoxScored();
        if (boxesScored == NUM_BOXES) {
            setGameOver(true);
        }
    }
    
    private int getScoreOfHand(ScoreBox.HandType handType) {
        int score = 0;
        switch (handType) {
        case ONES:
            score = hand.getCount(Hand.ONES) * 1;
            break;
        case TWOS:
            score = hand.getCount(Hand.TWOS) * 2;
            break;
        case THREES:
            score = hand.getCount(Hand.THREES) * 3;
            break;
        case FOURS:
            score = hand.getCount(Hand.FOURS) * 4;
            break;
        case FIVES:
            score = hand.getCount(Hand.FIVES) * 5;
            break;
        case SIXES:
            score = hand.getCount(Hand.SIXES) * 6;
            break;
        case THREE_OF_A_KIND:
            if (hand.isThreeOfAKind()) {
                score = hand.getSum();
            }
            break;
        case FOUR_OF_A_KIND:
            if (hand.isFourOfAKind()) {
                score = hand.getSum();
            }
            break;
        case FULL_HOUSE:
            if (hand.isFullHouse()) {
                score = FULL_HOUSE_VALUE;
            }
            break;
        case SHORT_STRAIGHT:
            if (hand.isShortStraight()) {
                score = SHORT_STRAIGHT_VALUE;
            }
            break;
        case LONG_STRAIGHT:
            if (hand.isLongStraight()) {
                score = LONG_STRAIGHT_VALUE;
            }
            break;
        case FIVE_OF_A_KIND:
            if (hand.isFiveOfAKind()) {
                score = KIND5_VALUE;
                lastBoxWasKind5 = true;
                /* fiveOfAKindCount can only be incremented here until 
                 * fiveOfAKindBonusEnabled becomes true */
                ++kind5Count;
            }
            break;
        case CHANCE:
            score = hand.getSum();
            break;
        default:
            throw new IllegalStateException("Illegal ScoreBox BoxType");
        }
        return score;
    }

    public void updateGrandTotal()
    {
        grandTotalBox.setScore(multipleOne.getScore()
                + multipleTwo.getScore() + multipleThree.getScore());
    }

    private void processKind5Bonus()
    {
        ResultBox box = kind5BonusBoxes[lastBoxScored.getColumn()];
        box.addScore(KIND5_VALUE);
        lastKind5BonusBox = box;
        lastBoxWasKind5 = true;
        ++kind5Count;
    }

    private void updateColumnOfLastBoxScored() {
        int column = lastBoxScored.getColumn();
        int score = lastBoxScored.getScore();
        int upperTotal, lowerTotal;
        if (lastBoxScored.isUpper()) {
            int subtotal = (unDoing) ? upperSubtotalBoxes[column].subtractScore(score)
                    : upperSubtotalBoxes[column].addScore(score);
            int bonus = subtotal < UPPER_PAR ? 0 : UPPER_BONUS;
            if (bonus > 0) {
                upperBonusBoxes[column].setScore(UPPER_BONUS);
            }
            upperTotal = upperTotalBoxes[column].setScore(subtotal + bonus);
            lowerTotal = lowerTotalBoxes[column].getScore();
        } else {
            lowerTotal = (unDoing) ? lowerTotalBoxes[column].subtractScore(score)
                    : lowerTotalBoxes[column].addScore(score);
            upperTotal = upperTotalBoxes[column].getScore();
        }
        int combinedTotal = upperTotal + lowerTotal +
                kind5BonusBoxes[column].getScore();
        combinedTotalBoxes[column].setScore(combinedTotal);
        int oldColumnScore = multipliedTotalBoxes[column].getScore();
        int newColumnScore = multipliedTotalBoxes[column].setScore(
                combinedTotal * COLUMN_MULTIPLIERS[column]);
        grandTotalBox.addScore(newColumnScore - oldColumnScore);
    }
       
    public void reset()
    {
        boxesScored = 0;
        gameOver = false;
        lastBoxScored = null;
        lastBoxWasKind5 = false;
        lastKind5BonusBox = null;
        kind5BonusEnabled = false;
        kind5Count = 0;
        gameRecord = null;
    
        for (ScoreBox[] column : scoreBoxes) {
            for (ScoreBox box : column) {
                box.eraseScore();
            }
        }
        for (ResultBox[] row : resultBoxes) {
            for (ResultBox box : row) {
                box.setScore(0);
            }
        }
        lowerBonusOne.setText("");
        lowerBonusTwo.setText("");
        lowerBonusThree.setText("");
        upperBonusOne.setText("");
        upperBonusTwo.setText("");
        upperBonusThree.setText("");
        grandTotalBox.setScore(0);
        
        unHightlightPlayerName();

    }
    
    public void undoScoreEntered()
    {
        if (lastBoxScored == null) {
            return;
        }
        unDoing = true;
        setGameOver(false);
        
        // handle five of a kind bonus, if applicable
        if (lastBoxWasKind5) {
            if (--kind5Count == 2) {
                kind5BonusEnabled = false;
            }
            if (lastKind5BonusBox != null && lastKind5BonusBox
                    .subtractScore(KIND5_VALUE) == 0) {
                lastKind5BonusBox.setText("");
            }
        }

        lastBoxWasKind5 = false;
        updateColumnOfLastBoxScored();
        lastBoxScored.eraseScore();
        lastBoxScored = null;
        unDoing = false;
        decrementBoxesScored();
    }
    
    public GameRecord getGameRecord() {
        return gameRecord;
    }

    public int getScore()
    {
        return this.grandTotalBox.getScore();
    }
    
    public int getScore(int row, int column) {
        if (row == KIND5_BONUS_ROW) {
            return kind5BonusBoxes[column].getScore();
        }
        return scoreBoxes[column][row].getScore();
    }
    
    public int getKind5Count()
    {
        return this.kind5Count;
    }

    public void clearLastBoxScored()
    {
        this.lastBoxScored = null;
    }

    public ScoreBox getLastBoxScored()
    {
        return this.lastBoxScored;
    }
    
    public Font getPlayerFont() {
        return playerName.getFont();
    }
    
    public void setPlayerFont(Font font) {
        playerName.setFont(font);
    }

    public void setPlayerName(String playerName)
    {
        this.playerName.setText(playerName);
    }

    public String getPlayerName()
    {
        return this.playerName.getText();
    }

    public boolean isUsingHighlights()
    {
        return usingHighlights;
    }

    public void setUsingHighlights(boolean usingHighlights)
    {
        this.usingHighlights = usingHighlights;
        if (!usingHighlights) {
            unHightlightPlayerName();
            setBorder(null);
        } else {
            setBorder(panelStandardBorder);
        }
    }

    public void highlightPlayerName()
    {
        playerName.setBorder(nameHighlightBorder);
    }

    public void unHightlightPlayerName()
    {
        playerName.setBorder(nameStandardBorder);
    }

    private void incrementBoxesScored()
    {
        this.propertySupport.firePropertyChange(
                PROP_SCORE_ENTERED, boxesScored, ++boxesScored);
    }

    private void decrementBoxesScored()
    {
        this.propertySupport.firePropertyChange(
                PROP_SCORE_ENTERED, boxesScored, --boxesScored);
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        if (usingHighlights) {
            if (active) {
                highlightPlayerName();
                setBorder(panelHighLightBorder);
            } else {
                unHightlightPlayerName();
               setBorder(panelStandardBorder);
            }
        }
        this.active = active;
    }

    public Hand getHand()
    {
        return hand;
    }

    public void setHand(Hand hand)
    {
        this.hand = hand;
    }

    public boolean isGameOver()
    {
        return gameOver;
    }
    
    public boolean isGameInProgress() {
        return !gameOver && boxesScored > 0;
    }

    public void setGameOver(boolean gameOver)
    {
        boolean old = this.gameOver;
        this.gameOver = gameOver;
        if (gameOver) {
            gameRecord = new GameRecord(this);
        }
        propertySupport.firePropertyChange(PROP_GAME_OVER, old, gameOver);
    }
    
    public void handleNewTurn() {
        setEnabled(true);
    }
    
    public int[] getInts() {
        int[] array = new int[5];     
        int ones = 0;
        int fours = 0;
        int kind3 = 0;
        int kind4 = 0;
        int chance = 0;
        int count = 0;
        
        for (int col = 0; col < COLUMNS; ++col) {
            // ones, twos, threes
            for (int row = ONES_ROW; row <= THREES_ROW; ++row) {
                count = scoreBoxes[col][row].getScore() / (row + 1);
                ones = ones << COUNT_SHIFT | count;
            }
            // fours, fives, sixes
            for (int row = FOURS_ROW; row <= SIXES_ROW; ++row) {
                count = scoreBoxes[col][row].getScore() / (row + 1);
                fours = fours << COUNT_SHIFT | count;
            }
            // three of a kind
            kind3 = kind3 << VALUE_SHIFT | scoreBoxes[col][KIND3_ROW].getScore();
            // four of a kind, full house, short/long straights, five of a kind
            kind4 = kind4 << VALUE_SHIFT | scoreBoxes[col][KIND4_ROW].getScore();
            for (int row = FULL_HOUSE_ROW; row <= KIND5_ROW; ++row) {
                kind4 = kind4 << 1 | (scoreBoxes[col][row].getScore() > 0 ? TRUE : FALSE);
            }
            // chance, five of a kind bonus
            chance = chance << VALUE_SHIFT |scoreBoxes[col][CHANCE_ROW].getScore();
            chance = chance << BONUS_SHIFT |
                    (kind5BonusBoxes[col].getScore() /
                            ScoreConstants.KIND5_VALUE);
        }
        array[ONES_TWOS_THREES] = ones;
        array[FOURS_FIVES_SIXES] = fours;
        array[KIND3] = kind3;
        array[KIND4_FULL_SHORT_LONG_KIND5] = kind4;
        array[CHANCE_BONUS] = chance;
        
        return array;
    }
    
    public void setStateFromGameRecord(GameRecord record) {
        reset();
        this.gameRecord = record;
        setPlayerName(record.getName());
        int[] ints = record.getInts();
        int ones = ints[ONES_TWOS_THREES];
        int fours = ints[FOURS_FIVES_SIXES];
        int kind3 = ints[KIND3];
        int kind4 = ints[KIND4_FULL_SHORT_LONG_KIND5];
        int chance = ints[CHANCE_BONUS];

        for (int col = COLUMNS - 1; col >= 0; --col) {
            // threes, twos, ones
            for (int row = THREES_ROW; row >= ONES_ROW; --row) {
                scoreBoxes[col][row].setScore((ones & COUNT_MASK) * (row + 1));
                ones >>= COUNT_SHIFT;
            }
            // sixes, fives, fourse
            for (int row = SIXES_ROW; row >= FOURS_ROW; --row) {
                scoreBoxes[col][row].setScore((fours & COUNT_MASK) * (row + 1));
                fours >>= COUNT_SHIFT;
            }
            // three of a kind
            scoreBoxes[col][KIND3_ROW].setScore(kind3 & VALUE_MASK);
            kind3 >>= VALUE_SHIFT;
            // five of a kind, long/short straights, full house, four of a kind
            int score = 0;
            for (int row = KIND5_ROW; row >= FULL_HOUSE_ROW; --row) {
                if ((kind4 & TRUE) == TRUE) {
                    switch(row) {
                    case FULL_HOUSE_ROW:
                        score = FULL_HOUSE_VALUE;
                        break;
                    case SHORT_STRAIGHT_ROW:
                        score = SHORT_STRAIGHT_VALUE;
                        break;
                    case LONG_STRAIGHT_ROW:
                        score = LONG_STRAIGHT_VALUE;
                        break;
                    case KIND5_ROW:
                        score = KIND5_VALUE;
                        break;
                    }
                    scoreBoxes[col][row].setScore(score);
                } else {
                    scoreBoxes[col][row].setScore(0);
                }
                kind4 >>= 1;
            }
            scoreBoxes[col][KIND4_ROW].setScore(kind4 & VALUE_MASK);
            kind4 >>= VALUE_SHIFT;
            // five of a kind bonus, chance
            int count = chance & BONUS_MASK;
            if (count > 0) {
                kind5BonusBoxes[col].setScore(count * KIND5_VALUE);
            }
            chance >>= BONUS_SHIFT;
            scoreBoxes[col][CHANCE_ROW].setScore(chance & VALUE_MASK);
            chance >>= VALUE_SHIFT;
        }
        restoreResultBoxes();
    }
      
    private void restoreResultBoxes() {
        
        for (int column = 0; column < 3; ++column) {
            // set upper column
            for (int row = ONES_ROW; row <= SIXES_ROW; ++row) {
                upperSubtotalBoxes[column]
                        .addScore(scoreBoxes[column][row].getScore());
            }
            int upperTotal = upperSubtotalBoxes[column].getScore();
            if (upperTotal >= UPPER_PAR) {
                upperTotal += upperBonusBoxes[column].addScore(UPPER_BONUS);
            }
            upperTotalBoxes[column].setScore(upperTotal);
            
            // set lower column
            for (int row = KIND3_ROW; row <= CHANCE_ROW; ++row) {
                lowerTotalBoxes[column]
                        .addScore(scoreBoxes[column][row].getScore());
            }
                     
            // set the combined total and multiplied total and add them to grand total
            int total = upperTotal + lowerTotalBoxes[column].getScore() +
                    kind5BonusBoxes[column].getScore();
            grandTotalBox.addScore(
                    multipliedTotalBoxes[column].addScore(
                            COLUMN_MULTIPLIERS[column] * 
                            combinedTotalBoxes[column].addScore(total)));
        }
        
    }
    
    public void doDummyGame() {
        setGameOver(false);
        Random random = new Random();
        for (int col = 0; col < COLUMNS; ++col) {
            for (int row = ONES_ROW; row <= SIXES_ROW; ++row) {
                scoreBoxes[col][row].setScore((random.nextInt(5) + 1) * (row + 1));
            }
            scoreBoxes[col][KIND3_ROW].setScore(random.nextInt(31));
            scoreBoxes[col][KIND4_ROW].setScore(random.nextInt(31));
            scoreBoxes[col][CHANCE_ROW].setScore(random.nextInt(31));
            scoreBoxes[col][FULL_HOUSE_ROW].setScore(random.nextDouble() < 0.7 ? FULL_HOUSE_VALUE : 0);
            scoreBoxes[col][SHORT_STRAIGHT_ROW].setScore(random.nextDouble() < 0.7 ? SHORT_STRAIGHT_VALUE : 0);
            scoreBoxes[col][LONG_STRAIGHT_ROW].setScore(random.nextDouble() < 0.7 ? LONG_STRAIGHT_VALUE : 0);
            scoreBoxes[col][KIND5_ROW].setScore(random.nextDouble() < 0.5 ? KIND5_VALUE : 0);
        }
        restoreResultBoxes();
        for (int col = 0; col < COLUMNS; ++col) {
            if (scoreBoxes[col][KIND5_ROW].getScore() > 0) {
                ++kind5Count;
            }
        }
        setGameOver(true);
        
    }
 
    @Override
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener)
    {
        this.propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener)
    {
        this.propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public void mousePressed(MouseEvent evt)
    {
        if (isEnabled() && isActive()) {
            Component component
                    = SwingUtilities.getDeepestComponentAt(this, evt.getX(), evt.getY());
            try {
                setScoreBox((ScoreBox) component);
            } catch (ClassCastException ex) {
                // do nothing, component simply wasn't a ScoreBox
            }
        }
    }
    
    private void initBorders() {
        if (nameHighlightBorder == null) {
            // define the border colors
            Color standardColor = UIManager.getDefaults().getColor(
                "Button.background");
//            int r = 255, g = 69, b = 0;
            int r = 0, g = 153, b = 0;
            Color highlightColor = new Color(r, g, b);
            float[] hsb = Color.RGBtoHSB(r, g, b, null);
            // reduce the saturation for the etched border
            hsb[1] /= 3;
            Color highlightEtchColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
            
            // define highlight border for the player name label
            nameHighlightBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(
                        BevelBorder.RAISED, null,
                        new java.awt.Color(204, 204, 204), null, null),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                standardColor, 4),
                        BorderFactory.createEtchedBorder(
                                highlightEtchColor,
                                highlightColor)));
            
            // define standard border for the player name label
            nameStandardBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(
                        BevelBorder.RAISED, null,
                        new java.awt.Color(204, 204, 204), null, null),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                standardColor, 2),
                        BorderFactory.createEtchedBorder()));
        }
        
        if (panelHighLightBorder == null) {
            // define the panel borders
            Color highlightColor = new Color(0x353535);
            int thickness = 2;
            panelHighLightBorder = BorderFactory
                    .createLineBorder(highlightColor, thickness);
            panelStandardBorder = BorderFactory
                    .createEmptyBorder(thickness, thickness, thickness, thickness);
        }
    }
    
    private void initBoxArrays()
    {
        upperSubtotalBoxes = new ResultBox[]{upperSubOne, upperSubTwo,
            upperSubThree};
        upperBonusBoxes = new ResultBox[]{upperBonusOne, upperBonusTwo,
            upperBonusThree};
        upperTotalBoxes = new ResultBox[]{upperTotalOne, upperTotalTwo,
            upperTotalThree};
        lowerTotalBoxes = new ResultBox[]{lowerTotalOne, lowerTotalTwo,
            lowerTotalThree};
        kind5BonusBoxes = new ResultBox[]{lowerBonusOne, lowerBonusTwo,
            lowerBonusThree};
        combinedTotalBoxes = new ResultBox[]{combinedOne, combinedTwo,
            combinedThree};
        multipliedTotalBoxes = new ResultBox[]{multipleOne, multipleTwo,
            multipleThree};
        resultBoxes = new ResultBox[][]{upperSubtotalBoxes, upperBonusBoxes,
            upperTotalBoxes, lowerTotalBoxes, kind5BonusBoxes,
            combinedTotalBoxes, multipliedTotalBoxes};
        
        scoreBoxes = new ScoreBox[][]{
            {scoreBox1, scoreBox4, scoreBox7, scoreBox10, scoreBox13, scoreBox16,
             scoreBox19, scoreBox22, scoreBox25, scoreBox28, scoreBox31,
             scoreBox34, scoreBox37},
            {scoreBox2, scoreBox5, scoreBox8, scoreBox11, scoreBox14, scoreBox17,
             scoreBox20, scoreBox23, scoreBox26, scoreBox29, scoreBox32,
             scoreBox35, scoreBox38},
            {scoreBox3, scoreBox6, scoreBox9, scoreBox12, scoreBox15, scoreBox18,
             scoreBox21, scoreBox24, scoreBox27, scoreBox30, scoreBox33,
             scoreBox36, scoreBox39}
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        playerName = new javax.swing.JLabel();
        boxPanel = new javax.swing.JPanel();
        scoreBox1 = new ScoreBox(ScoreBox.HandType.ONES, COLUMN_ZERO, true);
        scoreBox2 = new ScoreBox(ScoreBox.HandType.ONES, COLUMN_ONE, true);
        scoreBox3 = new ScoreBox(ScoreBox.HandType.ONES, COLUMN_TWO, true);
        scoreBox4 = new ScoreBox(ScoreBox.HandType.TWOS, COLUMN_ZERO, true);
        scoreBox5 = new ScoreBox(ScoreBox.HandType.TWOS, COLUMN_ONE, true);
        scoreBox6 = new ScoreBox(ScoreBox.HandType.TWOS, COLUMN_TWO, true);
        scoreBox7 = new ScoreBox(ScoreBox.HandType.THREES, COLUMN_ZERO, true);
        scoreBox8 = new ScoreBox(ScoreBox.HandType.THREES, COLUMN_ONE, true);
        scoreBox9 = new ScoreBox(ScoreBox.HandType.THREES, COLUMN_TWO, true);
        scoreBox10 = new ScoreBox(ScoreBox.HandType.FOURS, COLUMN_ZERO, true);
        scoreBox11 = new ScoreBox(ScoreBox.HandType.FOURS, COLUMN_ONE, true);
        scoreBox12 = new ScoreBox(ScoreBox.HandType.FOURS, COLUMN_TWO, true);
        scoreBox13 = new ScoreBox(ScoreBox.HandType.FIVES, COLUMN_ZERO, true);
        scoreBox14 = new ScoreBox(ScoreBox.HandType.FIVES, COLUMN_ONE, true);
        scoreBox15 = new ScoreBox(ScoreBox.HandType.FIVES, COLUMN_TWO, true);
        scoreBox16 = new ScoreBox(ScoreBox.HandType.SIXES, COLUMN_ZERO, true);
        scoreBox17 = new ScoreBox(ScoreBox.HandType.SIXES, COLUMN_ONE, true);
        scoreBox18 = new ScoreBox(ScoreBox.HandType.SIXES, COLUMN_TWO, true);
        upperSubOne = new com.davidjarski.javatzee.scorepad.ResultBox();
        upperSubTwo = new com.davidjarski.javatzee.scorepad.ResultBox();
        upperSubThree = new com.davidjarski.javatzee.scorepad.ResultBox();
        upperBonusOne = new com.davidjarski.javatzee.scorepad.ResultBox();
        upperBonusTwo = new com.davidjarski.javatzee.scorepad.ResultBox();
        upperBonusThree = new com.davidjarski.javatzee.scorepad.ResultBox();
        upperTotalOne = new com.davidjarski.javatzee.scorepad.ResultBox();
        upperTotalTwo = new com.davidjarski.javatzee.scorepad.ResultBox();
        upperTotalThree = new com.davidjarski.javatzee.scorepad.ResultBox();
        scoreBox19 = new ScoreBox(ScoreBox.HandType.THREE_OF_A_KIND, COLUMN_ZERO, false);
        scoreBox20 = new ScoreBox(ScoreBox.HandType.THREE_OF_A_KIND, COLUMN_ONE, false);
        scoreBox21 = new ScoreBox(ScoreBox.HandType.THREE_OF_A_KIND, COLUMN_TWO, false);
        scoreBox22 = new ScoreBox(ScoreBox.HandType.FOUR_OF_A_KIND, COLUMN_ZERO, false);
        scoreBox23 = new ScoreBox(ScoreBox.HandType.FOUR_OF_A_KIND, COLUMN_ONE, false);
        scoreBox24 = new ScoreBox(ScoreBox.HandType.FOUR_OF_A_KIND, COLUMN_TWO, false);
        scoreBox25 = new ScoreBox(ScoreBox.HandType.FULL_HOUSE, COLUMN_ZERO, false);
        scoreBox26 = new ScoreBox(ScoreBox.HandType.FULL_HOUSE, COLUMN_ONE, false);
        scoreBox27 = new ScoreBox(ScoreBox.HandType.FULL_HOUSE, COLUMN_TWO, false);
        scoreBox28 = new ScoreBox(ScoreBox.HandType.SHORT_STRAIGHT, COLUMN_ZERO, false);
        scoreBox29 = new ScoreBox(ScoreBox.HandType.SHORT_STRAIGHT, COLUMN_ONE, false);
        scoreBox30 = new ScoreBox(ScoreBox.HandType.SHORT_STRAIGHT, COLUMN_TWO, false);
        scoreBox31 = new ScoreBox(ScoreBox.HandType.LONG_STRAIGHT, COLUMN_ZERO, false);
        scoreBox32 = new ScoreBox(ScoreBox.HandType.LONG_STRAIGHT, COLUMN_ONE, false);
        scoreBox33 = new ScoreBox(ScoreBox.HandType.LONG_STRAIGHT, COLUMN_TWO, false);
        scoreBox34 = new ScoreBox(ScoreBox.HandType.FIVE_OF_A_KIND, COLUMN_ZERO, false);
        scoreBox35 = new ScoreBox(ScoreBox.HandType.FIVE_OF_A_KIND, COLUMN_ONE, false);
        scoreBox36 = new ScoreBox(ScoreBox.HandType.FIVE_OF_A_KIND, COLUMN_TWO, false);
        scoreBox37 = new ScoreBox(ScoreBox.HandType.CHANCE, COLUMN_ZERO, false);
        scoreBox38 = new ScoreBox(ScoreBox.HandType.CHANCE, COLUMN_ONE, false);
        scoreBox39 = new ScoreBox(ScoreBox.HandType.CHANCE, COLUMN_TWO, false);
        lowerTotalOne = new com.davidjarski.javatzee.scorepad.ResultBox();
        lowerTotalTwo = new com.davidjarski.javatzee.scorepad.ResultBox();
        lowerTotalThree = new com.davidjarski.javatzee.scorepad.ResultBox();
        lowerBonusOne = new com.davidjarski.javatzee.scorepad.ResultBox();
        lowerBonusTwo = new com.davidjarski.javatzee.scorepad.ResultBox();
        lowerBonusThree = new com.davidjarski.javatzee.scorepad.ResultBox();
        combinedOne = new com.davidjarski.javatzee.scorepad.ResultBox();
        combinedTwo = new com.davidjarski.javatzee.scorepad.ResultBox();
        combinedThree = new com.davidjarski.javatzee.scorepad.ResultBox();
        multipleOne = new com.davidjarski.javatzee.scorepad.ResultBox();
        multipleTwo = new com.davidjarski.javatzee.scorepad.ResultBox();
        multipleThree = new com.davidjarski.javatzee.scorepad.ResultBox();
        grandTotalBox = new com.davidjarski.javatzee.scorepad.ResultBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setLayout(new java.awt.GridBagLayout());

        playerName.setBackground(new java.awt.Color(255, 255, 255));
        playerName.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        playerName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        playerName.setText("Player");
        playerName.setToolTipText("");
        playerName.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null), javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.background"), 2), javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 217, 228), new java.awt.Color(255, 75, 125)))));
        playerName.setMaximumSize(new java.awt.Dimension(132, 35));
        playerName.setMinimumSize(new java.awt.Dimension(132, 35));
        playerName.setOpaque(true);
        playerName.setPreferredSize(new java.awt.Dimension(132, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(playerName, gridBagConstraints);

        boxPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(227, 227, 227), new java.awt.Color(250, 250, 250), new java.awt.Color(102, 102, 102), new java.awt.Color(204, 204, 204)));
        boxPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox11, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox12, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox13, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox14, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox15, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox16, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox17, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox18, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(upperSubOne, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(upperSubTwo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(upperSubThree, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(upperBonusOne, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(upperBonusTwo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(upperBonusThree, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(upperTotalOne, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(upperTotalTwo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(upperTotalThree, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox19, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox20, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox21, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox22, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox23, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox24, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox25, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox26, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox27, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox28, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox29, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox30, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox31, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox32, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox33, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox34, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox35, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox36, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox37, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox38, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(scoreBox39, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(lowerTotalOne, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(lowerTotalTwo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(lowerTotalThree, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(lowerBonusOne, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(lowerBonusTwo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(lowerBonusThree, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(combinedOne, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(combinedTwo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(combinedThree, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(multipleOne, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(multipleTwo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        boxPanel.add(multipleThree, gridBagConstraints);

        grandTotalBox.setBackground(new java.awt.Color(153, 199, 127));
        grandTotalBox.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        grandTotalBox.setMinimumSize(new java.awt.Dimension(124, 52));
        grandTotalBox.setPreferredSize(new java.awt.Dimension(124, 52));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        boxPanel.add(grandTotalBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(boxPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel boxPanel;
    private com.davidjarski.javatzee.scorepad.ResultBox combinedOne;
    private com.davidjarski.javatzee.scorepad.ResultBox combinedThree;
    private com.davidjarski.javatzee.scorepad.ResultBox combinedTwo;
    private com.davidjarski.javatzee.scorepad.ResultBox grandTotalBox;
    private com.davidjarski.javatzee.scorepad.ResultBox lowerBonusOne;
    private com.davidjarski.javatzee.scorepad.ResultBox lowerBonusThree;
    private com.davidjarski.javatzee.scorepad.ResultBox lowerBonusTwo;
    private com.davidjarski.javatzee.scorepad.ResultBox lowerTotalOne;
    private com.davidjarski.javatzee.scorepad.ResultBox lowerTotalThree;
    private com.davidjarski.javatzee.scorepad.ResultBox lowerTotalTwo;
    private com.davidjarski.javatzee.scorepad.ResultBox multipleOne;
    private com.davidjarski.javatzee.scorepad.ResultBox multipleThree;
    private com.davidjarski.javatzee.scorepad.ResultBox multipleTwo;
    private javax.swing.JLabel playerName;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox1;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox10;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox11;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox12;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox13;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox14;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox15;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox16;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox17;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox18;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox19;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox2;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox20;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox21;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox22;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox23;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox24;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox25;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox26;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox27;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox28;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox29;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox3;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox30;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox31;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox32;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox33;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox34;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox35;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox36;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox37;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox38;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox39;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox4;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox5;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox6;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox7;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox8;
    private com.davidjarski.javatzee.scorepad.ScoreBox scoreBox9;
    private com.davidjarski.javatzee.scorepad.ResultBox upperBonusOne;
    private com.davidjarski.javatzee.scorepad.ResultBox upperBonusThree;
    private com.davidjarski.javatzee.scorepad.ResultBox upperBonusTwo;
    private com.davidjarski.javatzee.scorepad.ResultBox upperSubOne;
    private com.davidjarski.javatzee.scorepad.ResultBox upperSubThree;
    private com.davidjarski.javatzee.scorepad.ResultBox upperSubTwo;
    private com.davidjarski.javatzee.scorepad.ResultBox upperTotalOne;
    private com.davidjarski.javatzee.scorepad.ResultBox upperTotalThree;
    private com.davidjarski.javatzee.scorepad.ResultBox upperTotalTwo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }
}
