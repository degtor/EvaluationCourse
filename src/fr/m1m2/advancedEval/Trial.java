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

	//Participant presses space when he detects the outlier object
	private KeyListener spaceListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				Canvas canvas = experiment.getCanvas();
				canvas.removeKeyListener(spaceListener);

				// Log time that the space bar was hit. This is the moment they noticed the correct mark.
				timeStart = new Date();

				System.out.println("SPACE PRESSED: " + objectCount);
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

						// 1. Log the time taken to hit the space-bar after the image has been displayed (difference in ms)
						long reactionTime = timeStart.getTime() - timeStop.getTime();
						System.out.println("User reaction time is: " + reactionTime);
						exitLog(reactionTime);

						// 2. Remove the transparent target from the screen
						hidePlaceHolders();

						// Continue to next trial
						experiment.nextTrial();


					} else {
						//restart
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
		int small = 30;
		int large = 60;
		Color light = Color.LIGHT_GRAY;
		Color dark = Color.DARK_GRAY;

		ArrayList<CShape> allShapes = new ArrayList<CShape>();
//		ArrayList<CShape> placeHolders = new ArrayList<CShape>();

		// TARGET SHAPE
		int targetSize = small;
		Color targetColor = light;
		CShape targetShape = null;
		targetSize = Math.random() > 0.5 ? large : small;
		targetColor = Math.random() > 0.5 ? dark : light;
		//targetShape = new CEllipse(0, 0, targetSize, targetSize);
		//targetShape.setFillPaint(targetColor);
		//targetShape = new CEllipse(0, 0, targetSize, targetSize);
		targetShape = new CRectangle(0, 0, targetSize, targetSize);
		//targetShape.setFillPaint(targetColor);
		targetShape.rotateBy(45);


		// OTHER SHAPES
//		CEllipse object;
		CRectangle object;
		int size;
		Color color;
		if(visualVariable.equals("VV1")) {
			// size, all other shapes have a different size from the target, and have the same color than the target
			size = targetSize == small ? large : small;
			color = targetColor;
			for(int i = 0; i < objectCount-1; i++) {
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size);
				object.setFillPaint(color);
				allShapes.add(object);
			}
		} else if(visualVariable.equals("VV2")) {
			// color, all other shapes have the same size than the target, and have a different color from the target
			size = targetSize;
			color = targetColor == dark ? light : dark;
			for(int i = 0; i < objectCount-1; i++) {
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size);
				object.setFillPaint(color);
				allShapes.add(object);
			}
		} else if(visualVariable.equals("VV1VV2")) {
			// we have to ensure that only the target should be different from all the other shapes
			// this means that we have to ensure that there is at least two identical objects of each type:
			// {same size, different color}, {different size, same color}, {different size, different color}

			// at least two objects have the same size and a different color
			color = targetColor == dark ? light : dark;
			size = targetSize;
			for(int i = 0; i < 2; i++) {
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size);
				object.setFillPaint(color);
				allShapes.add(object);
			}
			// at least two objects have the same color and a different size
			size = targetSize == small ? large : small;
			color = targetColor;
			for(int i = 0; i < 2; i++) {
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size);
				object.setFillPaint(color);
				allShapes.add(object);
			}
			// at least two objects have a different color and a different size
			size = targetSize == small ? large : small;
			color = targetColor == dark ? light : dark;
			for(int i = 0; i < 2; i++) {
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size);
				object.setFillPaint(color);
				allShapes.add(object);
			}
			// there are at least six objects in the list at this point
			for(int i = 6; i < (objectCount-1); i++) {
				size = Math.random() > 0.5 ? small : small * 2;
				if(size == targetSize) {
					color = targetColor == dark ? light : dark;
				} else {
					color = Math.random() > 0.5 ? light : dark;
				}
//				object = new CEllipse(0, 0, size, size);
				object = new CRectangle(0,0,size,size);
				object.setFillPaint(color);
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
				CRectangle placeholder = canvas.newRectangle(i*cellSize, j*cellSize, (20), (20));
//				placeholder.translateBy(-10, -10);
				placeholder.setFillPaint(Color.lightGray);
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

	protected void exitLog(long reactionTime) {
		long currentTime = new Date().getTime();
		// Open logfile
		PrintWriter pwLog = experiment.getPwLog();

		System.out.println(pwLog);
//		try(FileWriter fw = new FileWriter("experiment_results.csv", true);
//			BufferedWriter bw = new BufferedWriter(fw); PrintWriter out = new PrintWriter(bw);)
//		try {
			System.out.println("Writing to file Block: " + block);

//			String header =
//							experiment.getParticipant() + "\t"
//							+ block + "\t"
//							+ trial + "\t"
//							+ visualVariable + "\t"
//							+ objectCount + "\t"
//							+ reactionTime + "\n";
			String header = currentTime + "\t"
					+ experiment.getParticipant()+ "\t"
					+ block + "\t"
					+ trial+ "\t"
					+"Difficulty\t"
					+"Device\t"
					+ reactionTime + "\t"
					+"Error\t"
					+"Practice\n";


			pwLog.print(header);
			pwLog.flush();

//		}
//		catch (FileNotFoundException e) {
//			e.printStackTrace();
//			System.out.println("Could not open log file.");
//
//		}
	}


}
