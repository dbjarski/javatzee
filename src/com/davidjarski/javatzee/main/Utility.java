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
package com.davidjarski.javatzee.main;

import java.awt.Component;

public class Utility
{
    
    /**
     * Sets both the minimum and preferred sizes of the component to the
     * <code>Dimension</code> returned by <code>getPreferredSize()</code>.
     * <p>
     * This method should only be called after all components have been initialized
     * (i.e. after the call to <code>initComponents()</code>.   
     * <p>
     * Note that these sizes only provide hints for the sizing of components,
     * and may be ignored by some layout managers.
     * 
     * @param component the component whose sizes are to be set.
     */
    public static void lockComponentSize(Component component) {
        component.setPreferredSize(component.getPreferredSize());
        component.setMinimumSize(component.getPreferredSize());
    }
}
