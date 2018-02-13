package fr.m1m2.advancedEval;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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

//		//int targetIndex = (int)Math.min(Math.random() * objectCount, objectCount-1);
//		int small = 30;
//		int large = 60;
//		Color light = Color.LIGHT_GRAY;
//		Color dark = Color.DARK_GRAY;
//
//		ArrayList<CShape> allShapes = new ArrayList<>(CShape);
//
//		//Target shape
//		int targetSize = small;
//		Color targetColor = light;
//		CShape targetShape = null;
//		targetSize = Math.random() > 0.5 ? large : small;
//		targetColor = Math.random() > 0.5 ? dark : light;
//		targetShape = new CEllipse(0, 0, targetSize, targetSize);
//		targetShape.setFillPaint(targetColor);
//
//		//Other shapes
//		if(visualVariable.equals("VV1")){
//			size = targetSize == small ? large : small;
//			color = targetColor;
//			for (int i = 0; i < objectCount-1; i++) {
//				object = new CEllipse(0, 0, size, size);
//				object.setFillPaint(color);
//			}
//		} else if (visualVariable.equals("VV2")) {
//
//		} else if (visualVariable.equals("VV1VV2")) {
//
//		}
//
//		//there are at least six object in the list at this point
//		for(int i = 6; i <(objectCount-1); i++) {
//			size = Math.random() > 0.5 ? small : small * 2;
//			if(size == targetSize) {
//
//			}
//		}


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

		int xCoord = 0;
		int yCoord = 0;
		int row = 0;
		int col = 0;

		CRectangle storeShapes[] = new CRectangle[objectCount];

		//TODO add random number and pick which one of the shapes that is the target
		// 6 Participants!

		if (objectCount % 2 == 0) {
			row = objectCount/2;
			col = objectCount/2;
		} else {
			row = Math.round(objectCount/2);
			col = objectCount-row;
			System.out.println("Row: "+ row + "Col: " + col);
		}

		for (int x = 0; x <= row; x++) {
			xCoord= xCoord+50;
			for (int y = 0; y <= col; y++) {
				yCoord= yCoord+50;
				//canvas.addShape(createShape(xCoord,yCoord));
			}
			canvas.addShape(createShape(xCoord,yCoord));
		}

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
