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
public class Natty extends Robot {

    /**
     * @param args the command line arguments
     */
    public void run() {
        turnLeft(getHeading());
        while(true) {
            ahead(1000);        // Acciones secuenciales, poco efectivo
            turnRight(90);
        }
    }
    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
    }
    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(180);
    }
}
