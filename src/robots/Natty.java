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
    public void run(){
        setBodyColor(Color.RED);                              // Natty customizado (Librería Utils de Robocode)
        setRadarColor(Color.RED);
        setGunColor(Color.YELLOW);
        setBulletColor(Color.YELLOW);
     
        //turnLeft(getHeading());
        setTurnRadarRight(Double.POSITIVE_INFINITY);            // Gira el radar a la derecha por grados en la próxima ejecución
        setAdjustGunForRobotTurn(true);                         // El cañón permanece en la misma dirección mientras Natty gira
        setAdjustRadarForRobotTurn(true);                       // El radar permanece en la misma dirección mientras Natty gira 
        //setTurnRadarRight(270);
        
        while(true) {
            //setTurnGunRight(90);
            //setAhead(500);                                      // Movimiento de Natty 2000 píxeles
            //setTurnRight(90);                                   // Giro de Natty a la derecha en el píxel 10000
            //setTurnGunRight(270);
            scan();                                               // Escanea buscando enemigos mientras se mueve
            execute();                                            // Ejecuta
        }
    }
    public void onScannedRobot(ScannedRobotEvent e) {
        double direction = e.getBearingRadians()+getHeadingRadians();                   // direction = Grados respecto al enemigo + orientación de Natty
        double move = e.getVelocity()+Math.sin(e.getHeadingRadians()+direction);        // move = Velocidad de Natty + seno(Orientación de Natty + direction)
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());                        // El radar gira a la izq. los grados restantes del giro del radar

        setAhead(e.getDistance()-200);                                                  // Natty se mueve
        
        double gunmove = robocode.util.Utils.normalRelativeAngle(direction-getGunHeadingRadians()+(move/10));   
        // la función normaliza el ángulo a ángulo relativo, del cálculo de direction+move/10+orientación del cañón en grados
        setTurnGunRightRadians(gunmove);
        
        setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(direction-getHeadingRadians()+(move/getVelocity())));
        
        if (e.getEnergy() > 20){                                                        // Si Natty tiene más de un 20% de energia
            if (e.getDistance() < 200) setFire(Rules.MAX_BULLET_POWER);                 // Sólo dispara cuando el enemigo está cerca (a menos de 200 píxeles)
        }
        else {                                                                          // Por el contrario, si está a punto de morir
            if (e.getDistance() < 100) setFire(Rules.MAX_BULLET_POWER);                 // Sólo dispara cuando el enemigo está muy cerca, para no perder vida en disparos
        }
    }
    
    /*public void onHitWall(HitWallEvent e){
        setAhead(-1);
    }
    
    public void onHitBullet(HitBulletEvent e){
        setAhead(-1);
    }*/
}
