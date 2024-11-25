package com.myDomain.myLibrary;

import processing.core.PApplet;

/**
 * This is an example class within library myLibrary.
 * Make sure you rename this class as well as the name of the example package 'com.myDomain.myLibrary'
 * to your own library naming convention.
 */

public class SayHello {

	// parent is a reference to the parent sketch
	PApplet parent;

	/**
	 * a Constructor, usually called in the setup() method in your sketch to
	 * initialize and start the Library.
	 *
	 * @param theParent the parent PApplet
	 */
	public SayHello(PApplet theParent) {
		this.parent = theParent;
	}

	/**
	 * text returns text saying hello.
	 *
	 * @return a string saying hello
	 */
	public String text() {
		return "hello library.";
	}

}

