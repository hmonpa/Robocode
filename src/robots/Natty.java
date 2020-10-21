/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robots;
import robocode.*;
import robocode.util.Utils;
import java.awt.geom.*; 
import java.awt.Color;

/**
 * Robot creado para la asignatura PROP 2020-2021.
 * @author Hector Montesinos y Kilian Roig
 */

public class Natty extends AdvancedRobot
{
    
    // Variables globales
    private int dir = 1;
    private static double antDirEnemigo;
    private static double vidaEnemigo;
    private double direction;
    private double move;
    private double prediccionX;
    private double prediccionY;
    // Constante
    final static double BULLET_VELOCITY = 20-3*Rules.MAX_BULLET_POWER;
    
    ///////////////////////////    Métodos    ///////////////////////////
    
    /**
     * Metodo que se ejecuta al empezar el juego.
     */
    public void run(){
        
        // Cambiamos look de Natty
        nattyConEstilo();
        
        // Inicializaciones
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);             // Gira el radar a la derecha por grados en la próxima ejecución
        setAdjustGunForRobotTurn(true);                                 // El cañón permanece en la misma dirección mientras Natty gira
        setAdjustRadarForGunTurn(true);                                 // El radar permanece en la misma dirección mientras Natty gira 
        
        while(true) {
            scan();                     // Escanea buscando enemigos mientras se mueve
            execute();                  // Ejecuta
        }
    }
    
    /**
     * Método que es llamado cuando escaneamos un robot enemigo.
     * @param e Permite obtener informacion del robot enemigo.
     */
    public void onScannedRobot(ScannedRobotEvent e) {        
        // AnalizaSituacion
        analizaSituacion(e);
        // Calcula donde disparar a partir del metodo CircularTarget
        circularTarget(e);
        // Mueve el cañon y el radar a partir dela prediccion hecha anteriormente y dispara
        apuntarYDisparar(e);
    }
 
    /**
     * Método que es llamado cuando chocamos con una pared.
     * @param e Permite obtener informacion de la pared donde hemos chocado.
     */
    public void onHitWall(HitWallEvent e){
        dir = -dir;
        setBack(150*dir);
    }
    
    /**
     * Método que es llamado cuando recibimos un disparo
     * @param e Permite obtener informacion del disparo recibido
     */
    public void onHitByBullet(HitByBulletEvent e){
        setAhead(100*-dir);
    }
    
    /**
     * Metodo que es llamado cuando golpeamos a un enemigo
     * @param e Permite obtener informacion del impacto al enemigo
     */
    public void onBulletHit(BulletHitEvent e){
        vidaEnemigo-=Rules.MAX_BULLET_POWER*4;
    }

    /**
     * Método que analiza la situacion del enemigo para reajustar la orientación, velocidad y movimiento de radar y cañón
     * @param e Permite obtener informacón del enemigo
     */
    public void analizaSituacion(ScannedRobotEvent e) { 
        // Grados respecto al enemigo + orientación de Natty
        direction = e.getBearingRadians()+getHeadingRadians();  
        
        // Velocidad de Natty + seno(Orientación del enemigo + direction)
        move = e.getVelocity()+Math.sin(e.getHeadingRadians()+direction);        
        
        // El cañón gira a la derecha en la próx ejecución, el ángulo relativo de move - orientación de Natty
        setTurnGunRightRadians(Utils.normalRelativeAngle(move-getHeadingRadians()));
        
        // El radar gira a la izquierda en la próx ejecución, el ángulo restante en el giro del radar
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());                        
       
        // Máxima velocidad de Natty (píxeles/giros)
        setMaxVelocity(Rules.MAX_VELOCITY/getTurnRemaining());
        
        // Natty se mueve
        setAhead(100*dir);
    } 
    
    /**
     * Método que calcula donde disparar a partir de la posicion del enemigo utilizando una politica de target circular.
     * @param e Permite obtener información del enemigo.
     */
    public void circularTarget(ScannedRobotEvent e) { 
        // Estrategia utilizada
        double dirEnemigo = e.getHeadingRadians();
        double cambioDirEnemigo = dirEnemigo - antDirEnemigo;
        antDirEnemigo = dirEnemigo;
        
        // Primeras predicciones
        prediccionX = getX()+e.getDistance()*Math.sin(direction);
        prediccionY = getY()+e.getDistance()*Math.cos(direction);
        double tiempoPred = 0;
        // Mientras el tiempo que tarda la bala en llegar al enemigo, no sea superior al tiempo que necesita para llegar desde la posicion
        // actual hasta la posición predecida del tanque enemigo, seguimos haciendo predicciones
        while(Point2D.Double.distance(getX(), getY(), prediccionX, prediccionY) > (tiempoPred++)*BULLET_VELOCITY){
            // (static double) Returns the distance between two points (x1, y1, x2, y2)
            // Hacemos una predicción del eje X apartir de la direccion del enemigo y su velocidad
            prediccionX += e.getVelocity()*Math.sin(dirEnemigo);
            // Hacemos una predicción del eje Y apartir de la direccion del enemigo y su velocidad
            prediccionY += e.getVelocity()*Math.cos(dirEnemigo);
            
            dirEnemigo += cambioDirEnemigo;
            
            // Modificamos las predicciones X e Y en caso de que estemos en los bordes del mapa (no tiene sentido disparar
            // a un lugar al que el tanque enemigo no puede llegar)
            prediccionX=Math.max(Math.min(prediccionX,getBattleFieldWidth()-18),18);
            prediccionY=Math.max(Math.min(prediccionY,getBattleFieldHeight()-18),18);
        }
    }
    
        /**
     * Método que permite al tanque apuntar al lugar donde creemos que estara el tanque enemigo y disparar
     * @param e Permite obtener informacion del enemigo
     */
    public void apuntarYDisparar(ScannedRobotEvent e) { 
        // Obtención del ángulo absoluto mediante coordenadas x e y predecidas
        double angulo = Utils.normalAbsoluteAngle(Math.atan2(prediccionX - getX(), prediccionY - getY()));
        // Obtención del ángulo relativo del cañón (Gun) 
        double angulocañon = Utils.normalRelativeAngle(angulo - getGunHeadingRadians());
        setTurnGunRightRadians(angulocañon);
        
        // Natty dispara
        if (getEnergy()>20) setFire(Rules.MAX_BULLET_POWER);
        else if (e.getDistance() < 100) setFire(Rules.MIN_BULLET_POWER);
        
        // Obtención del ángulo relativo del radar
        double anguloradar = Utils.normalRelativeAngle(direction-getRadarHeadingRadians());
        setTurnRadarRightRadians(anguloradar);
    }
    
    /**
     * Método que viste a Natty bien guapo.
     */
    public void nattyConEstilo() {
        setBodyColor(Color.WHITE);                                       
        setRadarColor(Color.WHITE);
        setGunColor(Color.WHITE);
        setBulletColor(Color.WHITE);
    }
}