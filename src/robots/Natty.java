/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robots;
import java.awt.Color;
import robocode.*;
import robocode.util.Utils;
import java.awt.geom.*;
/**
 *
 * @author hector
 */
public class Natty extends AdvancedRobot
{
    
    // ATRIBUTOS
    
    private int dir = 1;
    int gunDirection = 1;
    double energiaPrevia = 100;
    private double marg = 20;
    
    
  
    
    // FUNCIONES
    
    private double apuntar(ScannedRobotEvent e){
        double angulo = e.getBearing() + getHeading();
        return angulo - getRadarHeading();
    }
    private void acercar(ScannedRobotEvent e){
        setTurnLeft(apuntar(e));
        setAhead(e.getDistance()-200);
        execute();
    }
    
    private boolean limites(){
        if (getX() <= marg || getY() <= marg || getX() >= getBattleFieldWidth()-marg || getY() >= getBattleFieldHeight()-marg) {
            return true;
        }
        else{
            return false;
        }
    }
    
    private double direccion(double x, double y){
        double angulo = Math.atan2(x - getX(), y - getY());
        return Utils.normalRelativeAngle(angulo-getHeading());
    }
    
    public void run(){
        
        // CONFIGURACIONES DE COLOR
        
        setBodyColor(Color.RED);                              // Natty customizado (Librería Utils de Robocode)
        setRadarColor(Color.RED);
        setGunColor(Color.YELLOW);
        setBulletColor(Color.YELLOW);
     
        
        // INICIALIZAIONES 
        
        setTurnRadarRight(Double.POSITIVE_INFINITY);            // Gira el radar a la derecha por grados en la próxima ejecución
        setAdjustGunForRobotTurn(true);                         // El cañón permanece en la misma dirección mientras Natty gira
        execute();
        setAdjustRadarForRobotTurn(true);                       // El radar permanece en la misma dirección mientras Natty gira 
        //setTurnGunRight(270);
        
        /// BUCLE MIENTRAS NO RECIBE EVENTOS...
        while(true) {
            movimientoBase();           // El robot hace un movimiento base independiente del enemigo
            scan();                                               // Escanea buscando enemigos mientras se mueve
            execute();                                            // Ejecuta
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        /*El radar dara giros de 360 grados en todo momento, en el momento que detecte al tanque enemigo, 
        se quedara comprobando ese segmento ( 90 grados), mientras siga detectando al tanque enemigo. 
        Si pasan un par de segundos sin detectar al robot, vuelve a dar giros de 360 grados.*/
        //double angleRadar = e.getBearing() + getHeading();
        //return angleRad
        
        
        /////////////////////////////// PARTE DE DETECCION ( HECTOR )  

        if (e.getName().toLowerCase().contains("Torre".toLowerCase())){
            fire(3);
        }
        setAhead(e.getDistance()-200);
        if (e.getEnergy() > 20){                             // Si Natty tiene más de un 20% de energia
            setFire(Rules.MAX_BULLET_POWER);                 // Sólo dispara cuando el enemigo está cerca (a menos de 200 píxeles)
        }
        else {                                               // Por el contrario, si está a punto de morir
            if (e.getDistance() < 100) setFire(Rules.MAX_BULLET_POWER);                 // Sólo dispara cuando el enemigo está muy cerca, para no perder vida en disparos
        }
        
        double direction = e.getBearingRadians()+getHeadingRadians();                   // direction = Grados respecto al enemigo + orientación de Natty
        double move = e.getVelocity()+Math.sin(e.getHeadingRadians()+direction);        // move = Velocidad de Natty + seno(Orientación de Natty + direction)
        //setTurnRadarLeftRadians(getRadarTurnRemainingRadians());                        // El radar gira a la izq. los grados restantes del giro del radar

                                                          // Natty se mueve
        //setAhead(200);
        
        double gunmove = robocode.util.Utils.normalRelativeAngle(direction-getGunHeadingRadians()+(move/10));   
        // la función normaliza el ángulo a ángulo relativo, del cálculo de direction+move/10+orientación del cañón en grados
        setTurnGunRightRadians(gunmove);
        
        setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(direction-getHeadingRadians()+(move/getVelocity())));

             
        //////////////////////// MOVIMIENTO PARA ESQUIVAR BALAS
        
        esquivar(e);
       
  }
    
    // Esquiva las balas enemigas
    public void esquivar(ScannedRobotEvent e) { 
        
        double diferenciaEnergia = energiaPrevia-e.getEnergy();
        // Si la energia del robot ha canviado menos de 3 significa que ha disparado
        if (diferenciaEnergia>0 && diferenciaEnergia<=3) {
         // Esquivamos
         dir = -dir;
         ahead((e.getDistance()/4+25)*dir);
        }
    
         // Guardamos el nivel actual de energia del rival
         energiaPrevia = e.getEnergy();     
    }
        
    // Movimiento base del robot
    public void movimientoBase() { 
        if (getTime()%20 == 0)  { 		//every twenty 'ticks'.  The % operator is a modulus.
            dir *= -1;		//reverse direction
            setAhead(dir*300);	//move in that direction
        }
       
}
     
    
    
    // Cuando el tanque se choca con la pared...
    public void onHitWall(HitWallEvent e){
        dir = -dir;
        setTurnRight(direccion(getBattleFieldWidth()/2, getBattleFieldHeight()/2));
        setBack(150*dir);
    }
}