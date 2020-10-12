/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robots;
import java.awt.Color;
import robocode.*;
import robocode.util.Utils;
/**
 *
 * @author hector
 */
public class Natty extends AdvancedRobot
{
    public void run() {
        setBodyColor(Color.BLACK);                              // Natty customizado
        setGunColor(Color.YELLOW);
        setRadarColor(Color.BLACK);
        
        turnLeft(getHeading());
        setTurnRadarRight(Double.POSITIVE_INFINITY);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForRobotTurn(true);
        
        while(true) {
            setAdjustRadarForRobotTurn(true);
            setTurnGunRight(90);
            setAhead(500);                                      // Movimiento de Natty 2000 píxeles
            setTurnRight(90);                                   // Giro de Natty a la derecha en el píxel 10000
            setTurnGunRight(270);
            execute();
        }
    }
    public void onScannedRobot(ScannedRobotEvent e) {
        setFire(Rules.MAX_BULLET_POWER);
    }
    
    public void onHitByBullet(HitByBulletEvent e) {
        //turnLeft(180);
    }
}
