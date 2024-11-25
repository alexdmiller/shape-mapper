import com.myDomain.myLibrary.*;

RandomCircles circles;
SayHello hello;

void setup() {
  size(400,400);
  smooth();

  circles = new RandomCircles(this);
  hello = new SayHello(this);
  
  PFont font = createFont("Arial", 40);
  textFont(font);
}

void draw() {
  background(0);
  noStroke();
  fill(255,0,0);
  circles.drawCircles();
  fill(255);
  text(hello.text(), 40, 200);
}
