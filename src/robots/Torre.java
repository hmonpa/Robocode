/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robots;
import robocode.*;
/**
 *
 * @author hector
 */
public class Torre extends Robot{
    private static double bearingThresold = 5;
    public void run(){
        turnLeft(getHeading());
        while (true){
            turnGunLeft(90);
            turnRadarLeft(90);
            //turnRight(90);
        }
    }
    
    double normalizeBearing(double bearing){
        while (bearing > 180) bearing -= 360;
        while (bearing < -180) bearing += 360;
        return bearing;
    }
    
    public void onScannedRobot(ScannedRobotEvent e){
        if (normalizeBearing(e.getBearing()) < bearingThresold){
            fire(1);
        }
    }
    
    public void onHitByBullet(HitByBulletEvent e){
        //turnLeft(180);
    }
}