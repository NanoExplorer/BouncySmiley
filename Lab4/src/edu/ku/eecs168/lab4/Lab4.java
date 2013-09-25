/****************************************************
 * File Name: 	Lab4.java
 * Author: 		Christopher Rooney [christopher@rooneyworks.com]
 * KUID:		2596119
 * Assignment:	EECS-169 Lab 4
 * Description: Android app displaying an alternating smiley face and frowny face 
 * Date:		9/25/2013 
 * 
 ****************************************************/


package edu.ku.eecs168.lab4;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import edu.ku.swingemu.AndroidJApplet;

public class Lab4 extends AndroidJApplet implements SensorEventListener {
	private SensorManager mSensorManager; /* As far as I can tell, the only thing this does
										   * is register my class to get sensor input and
										   * let me get a Sensor object for my mAccelerometer
										   * variable.
										   * 
										   */
	private Sensor mAccelerometer;		/*
										* This holds the Acceleromter Sensor object.
	 									* I think it's only used during the process of registering
	 									* Lab4 as a sensor listener.
	 									*
	 									*/
	

	//The value of counter has been initialized to help keep track of current frame
	public int counter=0;
	double velocityX =0, velocityY = 0, posX = 100, posY = 100, accelerationX = 0, accelerationY = 0;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
	    //import sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME/*I didn't know which of these to pick, but "GAME" sounded the most promising*/); 
	}
	
	
	

	public void paint(Graphics canvas) {
		// TODO write your code here
    
		
		velocityX += accelerationX; //acceleration is a change in velocity :)
		velocityY += accelerationY;
		
		posX += velocityX;			//velocity is a change in position :)
		posY += velocityY;
		
		
		/*********COLLISION DETECTION (Only checks screen borders (There isn't really anything else TO check...))******/
		if(posX + 200 >= canvas.getWidth()){
			velocityX = -velocityX + 1;
			posX = canvas.getWidth() - 200;
			
		}
		if(posY + 200 >= canvas.getHeight()) {
			velocityY = -velocityY + 1;
			posY = canvas.getHeight() - 200;
		}
		if(posX <= 0) {
			posX = 0;
			velocityX = -velocityX - 1;
		}
		if(posY<=0) {
			posY = 0;
			velocityY = -velocityY - 1;
		}
		
		// Clear canvas for redrawing the smiley
		canvas.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		//redraw smiley
		happyFace(canvas, (int)posX, (int)posY);

	}
	
	
	//Unused in this implementation
	public void sadFace(Graphics canvas, int x,  int y){
		
		//yellow circle
		canvas.setColor(Color.YELLOW);
		canvas.fillOval(x, y, 200, 200);
		
		canvas.setColor(Color.BLACK);
		//outline
		canvas.drawOval(x, y, 200, 200);
		//Eyes
		canvas.fillOval(x+55, y+50, 10, 20);
		canvas.fillOval(x+130, y+50, 10, 20);
        //un-Smile
		canvas.drawArc(x+50, y+130, 100, 50, 180, -180);
	}

	public void happyFace(Graphics canvas, int x, int y) {
		
		canvas.setColor(Color.YELLOW);
		//yellow circle
		canvas.fillOval(x, y, 200, 200);

		canvas.setColor(Color.BLACK);
		//outline
		canvas.drawOval(x, y, 200, 200);
		//Eyes
		canvas.fillOval(x+55, y+50, 10, 20);
		canvas.fillOval(x+130, y+50, 10, 20);
        //Smile
		canvas.drawArc(x+50, y+110, 100, 50, 180, 180);
		

	}




	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
		
		//can be ignored I guess
		//Since this is a demo, I don't really care about the accuracy, I guess.
		//It's more important that it works mostly than that it's super accurate
		
	}




	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		//Takes accelerometer data from event.values
		float x = event.values[1];
		float y = event.values[0];
		accelerationX = x;
		accelerationY = y;
	}
}
