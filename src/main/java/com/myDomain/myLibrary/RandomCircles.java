package com.myDomain.myLibrary;

import processing.core.*;
import org.apache.commons.math3.random.*;

/**
 * This is an example class within library myLibrary.
 * Make sure you rename this class as well as the name of the example package 'com.myDomain.myLibrary'
 * to your own library naming convention.
 */

public class RandomCircles {
	
	// parent is a reference to the parent sketch, and to Processing commands
	PApplet parent;

	/**
	 * a Constructor, usually called in the setup() method in your sketch to
	 * initialize and start the Library.
	 * 
	 * @param theParent the parent PApplet
	 */
	public RandomCircles(PApplet theParent) {
		this.parent = theParent;
	}


	/**
	 * drawCircles draws circles
	 * where their location is according to the Sobol Sequence,
	 * a statistical method found in Apache Commons Math.
	 *
	 * @param numCircles the number of circles to draw
	 */
	public void drawCircles(int numCircles) {
		RandomVectorGenerator generator = new SobolSequenceGenerator(2);
		for(int i=0; i<numCircles; i++) {
			double[] randomVector = generator.nextVector();
			// Processing commands and objects are available in the PApplet class,
			// via this.parent. Otherwise, they are not available.
			this.parent.circle(
					Math.round(randomVector[0]*this.parent.width),
					Math.round(randomVector[1]*this.parent.height),
					10
			);
		}
	}

	/**
	 * drawCircles draws circles
	 * where their location is according to the Sobol Sequence,
	 * a statistical method found in Apache Commons Math.
	 * It draws a default of 100 circles
	 */
	public void drawCircles() {
		drawCircles(100);
	}

}

