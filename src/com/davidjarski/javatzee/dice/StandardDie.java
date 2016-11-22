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
import java.util.Random;

public class StandardDie implements Die {
	private int min;
	private int max;
	private int value;
	private Random rand;
	
	public StandardDie() {
		this(1, 6);
	}
	
	public StandardDie(int min, int max) {
		this.min = min;
		this.max = max;
		this.rand = new Random();
	}
	
	public void roll() {
		value = rand.nextInt(max - min + 1) + min;
	}
	
	public int getValue() {
		return value;
	}
	
	public static void main(String[] args) {
		StandardDie die = new StandardDie();
		for (int i = 0; i < 10; i++) {
			die.roll();
			System.out.println(die.value);
		}
	}
}
