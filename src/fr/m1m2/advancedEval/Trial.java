package fr.m1m2.advancedEval;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;

import fr.lri.swingstates.canvas.*;
import fr.lri.swingstates.canvas.Canvas;

public class Trial {

	protected boolean practice = false;
	protected int block;
	protected int trial;
	protected String visualVariable;
	protected int objectCount;

	protected Date timerStart;
	protected Date timerStop;

	
	protected Experiment experiment;
	
	protected CExtensionalTag instructions = new CExtensionalTag() { };
	protected CExtensionalTag targetTag = new CExtensionalTag() { };
	
	public Trial(Experiment experiment, boolean practice, int block, int trial, String visualVariable, int objectCount) {
		this.practice = practice;
		this.block = block;
		this.trial = trial;
		this.visualVariable = visualVariable;
		this.objectCount = objectCount;
		this.experiment = experiment;
	}

	private KeyListener enterListener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					Canvas canvas = experiment.getCanvas();
					canvas.removeKeyListener(enterListener);
					hideInstructions();
					displayMainScene(objectCount);

					System.out.println("ENTER PRESSED: " + objectCount);
				}
			}
		};

	private KeyListener spaceListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				Canvas canvas = experiment.getCanvas();
				canvas.removeKeyListener(spaceListener);
				hideMainScene();

				System.out.println("SPACE PRESSED: " + objectCount);
				displayPlaceHolders();
			}
		}
	};

	private MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("Mouse Clicked: " + e.getX() + e.getY());
				hidePlaceHolders();
				//remove clicklistener
				CShape picked = experiment.getCanvas().pick(e.getPoint());
				if (picked != null ) {
					//There is a shape under the cursor
					if (picked.hasTag(targetTag)) {
						//There is a shape tagged with targetTag
						//under the cursor
					}
				}
				//experiment.nextTrial();
			}
	};

	public void displayInstructions() {
		Canvas canvas = experiment.getCanvas();
		CText text1 = canvas.newText(0, 0, "A scene with multiple shapes will get displayed", Experiment.FONT);
		CText text2 = canvas.newText(0, 50, "Identify the shape that is different from all other shapes", Experiment.FONT);
		CText text3 = canvas.newText(0, 100, "    1. Press Space bar", Experiment.FONT);
		CText text4 = canvas.newText(0, 150, "    2. Click on the identified shape", Experiment.FONT);
		CText text5 = canvas.newText(0, 200, "Do it AS FAST AND AS ACCURATELY AS POSSIBLE", Experiment.FONT);
		CText text6 = canvas.newText(0, 350, "--> Press Enter key when ready", Experiment.FONT.deriveFont(Font.PLAIN, 15));
		text1.addTag(instructions);
		text2.addTag(instructions);
		text3.addTag(instructions);
		text4.addTag(instructions);
		text5.addTag(instructions);
		text6.addTag(instructions);
		double textCenterX = instructions.getCenterX();
		double textCenterY = instructions.getCenterY();
		double canvasCenterX = canvas.getWidth()/2;
		double canvasCenterY = canvas.getHeight()/2;
		double dx = canvasCenterX - textCenterX;
		double dy = canvasCenterY - textCenterY;
		instructions.translateBy(dx, dy);
		canvas.setAntialiased(true);

		canvas.requestFocus();

		// ENTER-listener
		canvas.addKeyListener(enterListener);
	}

	public void hideInstructions() {
		Canvas canvas = experiment.getCanvas();
		canvas.removeShapes(instructions);
	}

	public void displayMainScene(int oc) {
		Canvas canvas = experiment.getCanvas();
		canvas.addKeyListener(spaceListener);
		makeShapes(oc);
	}

	public void displayPlaceHolders() {
		//TODO
	}

	public void hideMainScene() {
		//TODO
	}

	public void hidePlaceHolders () {
		//TODO
	}

	public void makeShapes(int oc) {
		Canvas canvas = experiment.getCanvas();
		objectCount = oc;

		int small = 10;
		int medium = 20;
		int large = 30;
		int x = 0;
		int y = 0;

		CRectangle storeShapes[] = new CRectangle[objectCount];
		for (int i = 0; i < objectCount; i++) {
			//storeShapes[i] = createShape (x,y);
			x = x+50;
			y = y+50;
			canvas.addShape(createShape(x,y));
		}



		//Rectangle


	}

	//TODO FOR THE PLACEHOLDERS
	//setDrawable(false)
	//setPickable(true)

	//setDrawable(true)
	//setPickable(false)

	// Create shapes for adding to list
	protected CRectangle createShape(int x, int y) {
		CRectangle shape = new CRectangle(x, y, 20, 30);
		shape.setFillPaint(Color.BLUE);
		//shape.addTag(visualMarks)
		return shape;
	}
	
}
