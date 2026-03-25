import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Clase que representa un Mars Rover.
 * Implementa la lógica de movimiento, consumo de potencia y registro de mandatos.
 */
public class Rover {
  private final String nombrePila;
  private double potenciaDisponible;
  private double potenciaInicial;
  private int posicionX;
  private int posicionY;
  private int cantRecargas;
  private int cantDetecciones;
  
  // Estructuras para el registro de mandatos
  private final ArrayList<ArrayList<String>> mandatosExitosos;
  private final ArrayList<ArrayList<String>> mandatosFallidos;

  // Constantes de diseño según requerimientos
  private static final int LIMITE_RECARGAS = 5;
  private static final double COSTO_MOVIMIENTO = 0.5;
  private static final double COSTO_DETECCION = 0.25;

  public Rover(String nombrePila) {
    this(nombrePila, 100.0);
  }

  public Rover(String nombrePila, double potencia) {
    this.nombrePila = nombrePila;
    this.potenciaInicial = potencia;
    this.potenciaDisponible = potencia;
    this.posicionX = 0;
    this.posicionY = 0;
    this.cantRecargas = 0;
    this.cantDetecciones = 0;
    this.mandatosExitosos = new ArrayList<>();
    this.mandatosFallidos = new ArrayList<>();
  }

  /**
   * Ejecuta la lógica de movimiento validando potencia y fugas.
   */
  private void procesarMovimiento(String direccion, int deltaX, int deltaY) {
    double costoTotal = COSTO_MOVIMIENTO + COSTO_DETECCION;

    if (potenciaDisponible < costoTotal) {
      registrarMandato("Mover " + direccion, false, "Potencia insuficiente");
      return;
    }

    if (detectarFuga()) {
      // Se cobra la potencia aunque falle por fuga (interpretación de ambigüedad)
      this.potenciaDisponible -= costoTotal; 
      registrarMandato("Mover " + direccion, false, "Fuga detectada");
      return;
    }

    this.posicionX += deltaX;
    this.posicionY += deltaY;
    this.potenciaDisponible -= costoTotal;
    registrarMandato("Mover " + direccion, true, "Exitoso");
  }

  public void moverNorte() { procesarMovimiento("Norte", 0, 1); }
  public void moverSur() { procesarMovimiento("Sur", 0, -1); }
  public void moverEste() { procesarMovimiento("Este", 1, 0); }
  public void moverOeste() { procesarMovimiento("Oeste", -1, 0); }

  private boolean detectarFuga() {
    this.cantDetecciones++;
    return new Random().nextBoolean(); // 50% probabilidad
  }

  public void recargar(double unidades) {
    if (cantRecargas < LIMITE_RECARGAS) {
      this.potenciaDisponible += unidades;
      this.cantRecargas++;
      registrarMandato("Recarga", true, "+" + unidades + " u");
    } else {
      registrarMandato("Recarga", false, "Límite alcanzado");
    }
  }

  private void registrarMandato(String tipo, boolean exito, String detalle) {
    ArrayList<String> registro = new ArrayList<>();
    registro.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    registro.add(tipo);
    registro.add(detalle);
    
    if (exito) mandatosExitosos.add(registro);
    else mandatosFallidos.add(registro);
  }

  public String consultarPosicion() {
    return String.format("(%d, %d)", posicionX, posicionY);
  }

  @Override
  public String toString() {
    return String.format("Rover %s | Potencia: %.2f | Pos: %s", 
        nombrePila, potenciaDisponible, consultarPosicion());
  }
}