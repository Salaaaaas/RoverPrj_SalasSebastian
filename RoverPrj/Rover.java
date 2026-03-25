import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Representa un vehículo explorador Mars Rover.
 * Esta clase gestiona el movimiento, el consumo de potencia, la detección de fugas
 * y el registro histórico de mandatos ejecutados.
 * * @author Sebastian Salas Leiton
 * @version 1.0
 */
public class Rover {
  private final String nombrePila;
  private double potenciaDisponible;
  private double potenciaInicial;
  private int posicionX;
  private int posicionY;
  private int cantRecargas;
  private int cantDetecciones;
  
  private final ArrayList<ArrayList<String>> mandatosExitosos;
  private final ArrayList<ArrayList<String>> mandatosFallidos;

  private static final int LIMITE_RECARGAS = 5;
  private static final double COSTO_MOVIMIENTO = 0.5;
  private static final double COSTO_DETECCION = 0.25;

  /**
   * Constructor por defecto que inicializa el Rover con 100 unidades de potencia.
   * @param nombrePila Identificador único del Rover.
   */
  public Rover(String nombrePila) {
    this(nombrePila, 100.0);
  }

  /**
   * Constructor que permite definir la potencia inicial del Rover.
   * @param nombrePila Identificador único del Rover.
   * @param potencia Cantidad inicial de energía disponible.
   */
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
   * Intenta mover el Rover hacia el Norte (incrementa Y).
   */
  public void moverNorte() { procesarMovimiento("Norte", 0, 1); }

  /**
   * Intenta mover el Rover hacia el Sur (decrementa Y).
   */
  public void moverSur() { procesarMovimiento("Sur", 0, -1); }

  /**
   * Intenta mover el Rover hacia el Este (incrementa X).
   */
  public void moverEste() { procesarMovimiento("Este", 1, 0); }

  /**
   * Intenta mover el Rover hacia el Oeste (decrementa X).
   */
  public void moverOeste() { procesarMovimiento("Oeste", -1, 0); }

  /**
   * Centraliza la lógica de movimiento, validando potencia y detección de fugas.
   * @param direccion Etiqueta del movimiento para el registro.
   * @param deltaX Cambio en el eje X.
   * @param deltaY Cambio en el eje Y.
   */
  private void procesarMovimiento(String direccion, int deltaX, int deltaY) {
    double costoTotal = COSTO_MOVIMIENTO + COSTO_DETECCION;

    if (potenciaDisponible < costoTotal) {
      registrarMandato("Mover " + direccion, false, "Potencia insuficiente");
      return;
    }

    if (detectarFuga()) {
      this.potenciaDisponible -= costoTotal; 
      registrarMandato("Mover " + direccion, false, "Fuga detectada");
      return;
    }

    this.posicionX += deltaX;
    this.posicionY += deltaY;
    this.potenciaDisponible -= costoTotal;
    registrarMandato("Mover " + direccion, true, "Exitoso");
  }

  /**
   * Simula la detección de fugas de calor mediante un generador aleatorio.
   * @return true si se detecta una fuga, false en caso contrario.
   */
  private boolean detectarFuga() {
    this.cantDetecciones++;
    return new Random().nextBoolean();
  }

  /**
   * Incrementa la potencia disponible del Rover si no se ha superado el límite de recargas.
   * @param unidades Cantidad de energía a sumar.
   */
  public void recargar(double unidades) {
    if (cantRecargas < LIMITE_RECARGAS) {
      this.potenciaDisponible += unidades;
      this.cantRecargas++;
      registrarMandato("Recarga", true, "+" + unidades + " u");
    } else {
      registrarMandato("Recarga", false, "Límite alcanzado");
    }
  }

  /**
   * Registra internamente los mandatos en las listas de éxito o fallo.
   * @param tipo El tipo de operación realizada.
   * @param exito Resultado de la operación.
   * @param detalle Descripción del resultado o del error.
   */
  private void registrarMandato(String tipo, boolean exito, String detalle) {
    ArrayList<String> registro = new ArrayList<>();
    registro.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    registro.add(tipo);
    registro.add(detalle);
    
    if (exito) mandatosExitosos.add(registro);
    else mandatosFallidos.add(registro);
  }

  /**
   * Devuelve la ubicación actual en formato de coordenadas.
   * @return String con formato (X, Y).
   */
  public String consultarPosicion() {
    return String.format("(%d, %d)", posicionX, posicionY);
  }

  /**
   * Obtiene la potencia actual del sistema.
   * @return Valor double de la potencia disponible.
   */
  public double getPotencia() {
    return potenciaDisponible;
  }

  @Override
  public String toString() {
    return String.format("Rover %s | Potencia: %.2f | Pos: %s", 
        nombrePila, potenciaDisponible, consultarPosicion());
  }
}
