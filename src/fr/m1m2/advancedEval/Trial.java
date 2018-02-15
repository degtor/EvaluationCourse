package fr.m1m2.advancedEval;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import fr.lri.swingstates.canvas.*;
import fr.lri.swingstates.canvas.Canvas;

public class Trial {

	protected boolean practice = false;
	protected int block;
	protected int trial;
	protected String visualVariable;
	protected int objectCount;
	
	protected Experiment experiment;

	protected CExtensionalTag visualMark = new CExtensionalTag() { };
	protected CExtensionalTag target = new CExtensionalTag() { };
	protected CExtensionalTag instructions = new CExtensionalTag() { };
	protected CExtensionalTag placeholders = new CExtensionalTag() { };

	protected Date timeStart;
	protected Date timeStop;
	protected int errors;

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

				}
			}
		};

	//Participant presses space when he detects the outlier object
	private KeyListener spaceListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				Canvas canvas = experiment.getCanvas();
				canvas.removeKeyListener(spaceListener);

				// Log time that the space bar was hit. This is the moment they noticed the correct mark.
				timeStart = new Date();

				displayPlaceHolders();
				canvas.addMouseListener(mouseListener);
			}
		}
	};

	//Used to confirm that the participant saw the right shape after pressing space
	private MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				CShape picked = experiment.getCanvas().pick(e.getPoint());

				if (picked != null ) {
					if (picked.hasTag(target)) {


						System.out.println("You clicked the target:" + e.getSource());

						long reactionTime = timeStart.getTime() - timeStop.getTime();
						System.out.println("User reaction time is: " + reactionTime);
						exitLog(reactionTime, errors);
						errors = 0;

						hidePlaceHolders();

						experiment.nextTrial();


					} else {
						//restart
						timeStart = new Date();
						errors++;
						hidePlaceHolders();
						displayMainScene(objectCount);
					}
				}
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
		// Record time that the user gets to start looking at images
		timeStop = new Date();

		// STEP 2 [begin]
		int targetIndex = (int)Math.min(Math.random() * objectCount, objectCount-1);
		int small = 20;
		int large = 40;
		int recFactor = 20;
		//Color light = Color.LIGHT_GRAY;
		//Color dark = Color.DARK_GRAY;

		ArrayList<CShape> allShapes = new ArrayList<CShape>();

		// TARGET SHAPE
		int targetSize = Math.random() > 0.5 ? large : small;
		//int rotate = 0;
		int targetOrientation = Math.random() > 0.5 ? 10 : 45;
		CShape targetShape = null;
		targetShape = new CRectangle(0, 0, targetSize, targetSize+recFactor);
		targetShape.rotateBy(targetOrientation);


		// OTHER SHAPES
//		CEllipse object;
		CRectangle object;
		int size;
		int rotate;
		//Color color;
		if(visualVariable.equals("VV1")) {
			// size, all other shapes have a different size from the target, and have the same orientation than the target
			size = targetSize == small ? large : small;
			//color = targetColor;
			for(int i = 0; i < objectCount-1; i++) {
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size+recFactor);
				//object.setFillPaint(color);
				object.rotateBy(45);
				allShapes.add(object);
			}
		} else if(visualVariable.equals("VV2")) {
			// color, all other shapes have the same size than the target, and have a different orientation from the target
			//color = targetColor == dark ? light : dark;
			rotate = targetOrientation == 10 ? 45 : 10;
			size = targetSize;
			for(int i = 0; i < objectCount-1; i++) {
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size+recFactor);
				//object.setFillPaint(color);
				object.rotateBy(rotate);
				allShapes.add(object);
			}
		} else if(visualVariable.equals("VV1VV2")) {
			//System.out.println("I am VV1VV2");
			// we have to ensure that only the target should be different from all the other shapes
			// this means that we have to ensure that there is at least two identical objects of each type:
			// {same size, different color}, {different size, same color}, {different size, different color}

			// at least two objects have the same size and a different orientation
			//color = targetColor == dark ? light : dark;
			size = targetSize;
			rotate = targetOrientation == 10 ? 45 : 10;
			for(int i = 0; i < 2; i++) {
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size+recFactor);
				object.rotateBy(rotate);
				//object.setFillPaint(color);
				allShapes.add(object);
			}
			// at least two objects have the same orientation and a different size
			size = targetSize == small ? large : small;
			rotate = targetOrientation ;
			for(int i = 0; i < 2; i++) {
				object = new CRectangle(0,0,size,size+recFactor);
				object.rotateBy(rotate);
				allShapes.add(object);
			}
			// at least two objects have a different orientation and a different size
			size = targetSize == small ? large : small;
			rotate = targetOrientation == 10 ? 45 : 10;
			for(int i = 0; i < 2; i++) {
				object = new CRectangle(0,0,size,size+recFactor);
				object.rotateBy(rotate);
				allShapes.add(object);
			}
			// there are at least six objects in the list at this point
			for(int i = 6; i < (objectCount-1); i++) {
				size = Math.random() > 0.5 ? small : small * 2;
				if(size == targetSize) {
					//color = targetColor == dark ? light : dark;
					rotate = targetOrientation == 10 ? 45 : 10;
				} else {
					//color = Math.random() > 0.5 ? light : dark;
					rotate = Math.random() > 0.5 ? 45 : 10;
				}
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size+recFactor);
				//object.setFillPaint(color);
				object.rotateBy(rotate);
				allShapes.add(object);
			}
		}

		Collections.shuffle(allShapes);
		allShapes.add(targetIndex, targetShape);

		// LAYOUT
		int cellSize = 100;
		int count = 0;
		for(int i = 0; i < Math.sqrt(objectCount); i++) {
			for(int j = 0; j < Math.sqrt(objectCount); j++) {

				// Draw placeholder behind the place where the mark will appear
				CEllipse placeholder = canvas.newEllipse(i*cellSize, j*cellSize, (30), (30));
//				placeholder.translateBy(-10, -10);
				placeholder.setFillPaint(Color.RED);
				placeholder.setFilled(false);
				placeholder.setOutlined(false);
				placeholder.addTag(placeholders);

				// add the shape itself
				CShape sh = allShapes.get(count);

				canvas.addShape(sh);
				if(count == targetIndex)  {
					sh.addTag(target);
					placeholder.addTag(target);

				}
				sh.addTag(visualMark);
				sh.translateTo(i*cellSize, j*cellSize);

				count++;
			}
		}

		canvas.translateBy(canvas.getWidth()/2 - visualMark.getCenterX(), canvas.getHeight()/2 - visualMark.getCenterY());
		// STEP 2 [end]

	}

	public void displayPlaceHolders() {
		Canvas canvas = experiment.getCanvas();
		canvas.removeShapes(visualMark);

		placeholders.setFilled(true);
	}

	public void hidePlaceHolders () {
		Canvas canvas = experiment.getCanvas();
		canvas.removeShapes(target);

		canvas.removeMouseListener(mouseListener);
		canvas.removeShapes(placeholders);
	}

	protected void exitLog(long reactionTime, int errors) {
		Date currentTime = new Date();
		PrintWriter pwLog = experiment.getPwLog();

			System.out.println("Writing to file Block: " + block);

			String header = currentTime.toString() + "\t"
					+ experiment.getParticipant()+ "\t"
					+ block + " \t"
					+ trial + " \t"
					+ objectCount + " \t"
					+ visualVariable + " \t"
					+ reactionTime + "\t"
					+ errors + "\t"
					+"Practice \n";


			pwLog.print(header);
			pwLog.flush();

	}


}
