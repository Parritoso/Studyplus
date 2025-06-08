# Programación Multihilo en Java

La programación multihilo permite ejecutar múltiples partes de un programa de forma concurrente, mejorando la capacidad de respuesta y el rendimiento de las aplicaciones. Java proporciona varias formas de lograr esto.

## Métodos para Crear Hilos

### 1. Extender la Clase `Thread`

Una forma de crear un nuevo hilo es definiendo una clase que extienda la clase `java.lang.Thread` y sobrescribiendo su método `run()`. El código que se ejecutará en el nuevo hilo se coloca dentro de este método.

```java
class MiHilo extends Thread {
    private String nombre;

    public MiHilo(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        System.out.println("Hilo " + nombre + " en ejecución.");
        for (int i = 0; i < 5; i++) {
            System.out.println("Hilo " + nombre + ": " + i);
            try {
                Thread.sleep(100); // Simula trabajo
            } catch (InterruptedException e) {
                System.out.println("Hilo " + nombre + " interrumpido.");
                return;
            }
        }
        System.out.println("Hilo " + nombre + " terminado.");
    }

    public static void main(String[] args) {
        MiHilo hilo1 = new MiHilo("Uno");
        MiHilo hilo2 = new MiHilo("Dos");
        hilo1.start(); // Inicia la ejecución del hilo
        hilo2.start();
    }
}
```

Al llamar al método `start()`, se crea un nuevo hilo de ejecución y se invoca el método `run()` en ese nuevo hilo.

### 2. Implementar la Interfaz `Runnable`

Una forma más flexible es implementar la interfaz `java.lang.Runnable`. Esto permite que tu clase extienda otra clase además de `Thread`. El código a ejecutar se coloca en el método `run()` de la interfaz `Runnable`. Luego, se crea un objeto `Thread` pasándole una instancia de la clase `Runnable`.

```java
class MiTarea implements Runnable {
    private String nombre;

    public MiTarea(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        System.out.println("Tarea " + nombre + " en ejecución.");
        for (int i = 0; i < 5; i++) {
            System.out.println("Tarea " + nombre + ": " + i);
            try {
                Thread.sleep(100); // Simula trabajo
            } catch (InterruptedException e) {
                System.out.println("Tarea " + nombre + " interrumpida.");
                return;
            }
        }
        System.out.println("Tarea " + nombre + " terminada.");
    }

    public static void main(String[] args) {
        Thread hilo1 = new Thread(new MiTarea("A"));
        Thread hilo2 = new Thread(new MiTarea("B"));
        hilo1.start();
        hilo2.start();
    }
}
```

### 3. Usar `Executors.newFixedThreadPool`

El `java.util.concurrent` package proporciona el `ExecutorService`, una interfaz para gestionar la ejecución de tareas. `Executors.newFixedThreadPool(int nThreads)` crea un pool de hilos con un número fijo de hilos. Es una forma más eficiente de gestionar hilos, ya que reutiliza los hilos existentes para ejecutar nuevas tareas, en lugar de crear un nuevo hilo para cada tarea.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TareaPool implements Runnable {
    private String nombre;

    public TareaPool(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        System.out.println("Tarea " + nombre + " ejecutada por " + Thread.currentThread().getName());
        try {
            Thread.sleep(200); // Simula trabajo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Tarea " + nombre + " interrumpida.");
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2); // Pool de 2 hilos

        for (int i = 1; i <= 5; i++) {
            executor.execute(new TareaPool("Tarea " + i));
        }

        executor.shutdown(); // Indica que no se aceptarán nuevas tareas
        while (!executor.isTerminated()) {
            // Espera a que todos los hilos terminen
        }
        System.out.println("Todas las tareas han terminado.");
    }
}
```

Es importante llamar a `executor.shutdown()` para liberar los recursos del pool de hilos una vez que no se necesiten más tareas.

**¿Por qué usar un Pool de Hilos?**

* **Reutilización de Hilos:** La creación de un nuevo hilo es una operación costosa en términos de recursos del sistema. Un pool de hilos reutiliza los hilos existentes para ejecutar múltiples tareas, lo que reduce la sobrecarga y mejora el rendimiento.
* **Gestión de la Concurrencia:** Un pool de hilos ayuda a limitar el número de hilos que se ejecutan simultáneamente, evitando la sobrecarga del sistema cuando se envían muchas tareas.
* **Mejor Capacidad de Respuesta:** Al reutilizar hilos, las tareas pueden comenzar a ejecutarse más rápidamente en comparación con la creación de un nuevo hilo para cada tarea.

**`Runtime.getRuntime().availableProcessors()`**

Este método estático de la clase `java.lang.Runtime` devuelve el número de procesadores (o núcleos lógicos) disponibles para la máquina virtual Java. Esta información es valiosa para determinar un tamaño adecuado para el pool de hilos.

```java
int numProcesadores = Runtime.getRuntime().availableProcessors();
System.out.println("Número de procesadores disponibles: " + numProcesadores);
ExecutorService executor = Executors.newFixedThreadPool(numProcesadores);
```

Generalmente, un tamaño de pool de hilos igual al número de procesadores disponibles es un buen punto de partida para tareas intensivas en CPU. Para tareas que involucran operaciones de E/S (como acceso a la red o al disco), un tamaño de pool mayor podría ser beneficioso, ya que los hilos pueden pasar tiempo esperando la finalización de estas operaciones.

`List<Future<?>> futures`

La interfaz `java.util.concurrent.Future` representa el resultado de una operación asíncrona. Cuando envías una tarea a un `ExecutorService` (ya sea implementando `Runnable` o `Callable`), el método `submit()` devuelve un objeto `Future`.

- `Future<?>`: El comodín ? indica que no se conoce el tipo específico del resultado de la tarea. Si la tarea implementa `Callable<T>`, donde T es el tipo del resultado, entonces el `Future` será de tipo `Future<T>`.
- `List<Future<?>> futures`: Es una lista que se utiliza para almacenar los objetos Future devueltos por cada tarea enviada al `ExecutorService`. Esto permite realizar un seguimiento del estado de cada tarea y obtener sus resultados (si los hay) una vez que se completan.

`futures.add(executor.submit(Runnable task))` y `futures.add(executor.submit(Callable<T> task))`

El método `submit()` del `ExecutorService` se utiliza para enviar tareas para su ejecución. Hay dos formas principales de `submit()`:

1. `executor.submit(Runnable task)`: Envía una tarea que no devuelve ningún resultado. El Future devuelto en este caso representa la finalización de la tarea. Puedes llamar a `future.get()` (que bloqueará hasta que la tarea termine y devolverá `null`) o `future.isDone()` para verificar si la tarea ha finalizado.

```java
Runnable miTareaRunnable = () -> {
    System.out.println("Ejecutando tarea Runnable.");
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    System.out.println("Tarea Runnable completada.");
};
Future<?> futureRunnable = executor.submit(miTareaRunnable);
futures.add(futureRunnable);
```

2. `executor.submit(Callable<T> task)`: Envía una tarea que devuelve un resultado de tipo T. La interfaz `Callable` es similar a `Runnable` pero su método `call()` puede lanzar una excepción y devuelve un valor. El `Future<T>` devuelto permite obtener el resultado de la tarea llamando a `future.get()`. Este método bloqueará hasta que la tarea se complete y devolverá el resultado.

```java
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

Callable<String> miTareaCallable = () -> {
    System.out.println("Ejecutando tarea Callable.");
    Thread.sleep(2000);
    return "Resultado de la tarea Callable";
};
Future<String> futureCallable = executor.submit(miTareaCallable);
futures.add(futureCallable);

// Más tarde, puedes obtener el resultado:
// String resultado = futureCallable.get(); // Puede lanzar InterruptedException o ExecutionException
// System.out.println("Resultado: " + resultado);
```

**Ejemplo Completo con `List<Future<?>>`:**

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EjemploThreadPoolConFutures {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int numProcesadores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numProcesadores);
        List<Future<?>> futures = new ArrayList<>();

        // Enviar tareas Runnable
        for (int i = 1; i <= 3; i++) {
            int tareaId = i;
            Runnable tareaRunnable = () -> {
                System.out.println("Ejecutando tarea Runnable " + tareaId + " en " + Thread.currentThread().getName());
                try {
                    Thread.sleep((long) (Math.random() * 2000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Tarea Runnable " + tareaId + " completada.");
            };
            futures.add(executor.submit(tareaRunnable));
        }

        // Enviar tareas Callable
        for (int i = 1; i <= 2; i++) {
            int tareaId = i + 3;
            Callable<String> tareaCallable = () -> {
                System.out.println("Ejecutando tarea Callable " + tareaId + " en " + Thread.currentThread().getName());
                Thread.sleep((long) (Math.random() * 3000));
                return "Resultado de Callable " + tareaId;
            };
            Future<String> futureCallable = executor.submit(tareaCallable);
            futures.add(futureCallable);
        }

        // Esperar a que todas las tareas terminen y obtener resultados (si los hay)
        System.out.println("Enviando todas las tareas. Esperando resultados...");
        for (Future<?> future : futures) {
            try {
                if (future.isDone()) {
                    System.out.println("Tarea completada. Resultado (si aplica): " + future.get());
                } else {
                    System.out.println("Tarea aún en ejecución.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Tarea interrumpida: " + e.getMessage());
            } catch (ExecutionException e) {
                System.err.println("Error al ejecutar la tarea: " + e.getMessage());
            }
        }

        executor.shutdown();
        executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);
        System.out.println("Todas las tareas han sido procesadas y el executor se ha apagado.");
    }
}
```

En este ejemplo, se muestra cómo enviar tanto tareas `Runnable` como `Callable` al `ExecutorService`, cómo almacenar los Futures resultantes en una lista y cómo iterar sobre ellos para esperar su finalización y obtener sus resultados.

El uso de `ExecutorService` con Futures proporciona un control más avanzado sobre la ejecución de tareas asíncronas, permitiendo rastrear su progreso, obtener sus resultados y gestionar su ciclo de vida de manera más eficiente.

## Acceso Concurrente y Sincronización

Cuando múltiples hilos acceden y modifican recursos compartidos, pueden surgir problemas de condiciones de carrera e inconsistencia de datos. Java proporciona mecanismos para controlar el acceso concurrente.

### 1. `synchronized`

La palabra clave `synchronized` se utiliza para crear secciones críticas de código o para sincronizar métodos completos. Solo un hilo puede ejecutar un bloque de código sincronizado o un método sincronizado para un objeto dado a la vez.

```java
class Contador {
    private int cuenta = 0;

    public synchronized void incrementar() {
        cuenta++;
    }

    public int getCuenta() {
        return cuenta;
    }
}
```

```java
class HiloIncremento implements Runnable {
    private Contador contador;

    public HiloIncremento(Contador contador) {
        this.contador = contador;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            contador.incrementar();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Contador contador = new Contador();
        Thread hilo1 = new Thread(new HiloIncremento(contador));
        Thread hilo2 = new Thread(new HiloIncremento(contador));

        hilo1.start();
        hilo2.start();

        hilo1.join(); // Espera a que hilo1 termine
        hilo2.join(); // Espera a que hilo2 termine

        System.out.println("Cuenta final: " + contador.getCuenta()); // Debería ser 2000
    }
}
```

### 2. `java.util.concurrent.ConcurrentHashMap`

Para colecciones que necesitan ser accedidas y modificadas concurrentemente de forma segura y eficiente, `ConcurrentHashMap` proporciona una implementación de `Map` thread-safe sin bloquear todo el mapa durante las operaciones de escritura. Utiliza un enfoque de segmentación y bloqueo a nivel de segmento para permitir múltiples escrituras simultáneas.

```java
import java.util.concurrent.ConcurrentHashMap;

public class EjemploConcurrentHashMap {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Integer> mapaConcurrente = new ConcurrentHashMap<>();

        Runnable tareaEscritura = () -> {
            for (int i = 0; i < 1000; i++) {
                mapaConcurrente.put("clave-" + i, i);
            }
        };

        Runnable tareaLectura = () -> {
            for (int i = 0; i < 1000; i++) {
                System.out.println("Leyendo clave-" + i + ": " + mapaConcurrente.get("clave-" + i));
            }
        };

        Thread hiloEscritura1 = new Thread(tareaEscritura);
        Thread hiloEscritura2 = new Thread(tareaEscritura);
        Thread hiloLectura = new Thread(tareaLectura);

        hiloEscritura1.start();
        hiloEscritura2.start();
        hiloLectura.start();

        hiloEscritura1.join();
        hiloEscritura2.join();
        hiloLectura.join();

        System.out.println("Tamaño final del mapa: " + mapaConcurrente.size());
    }
}
```

## Concurrencia en Aplicaciones Struts 2 y Servlet

Puedes utilizar la concurrencia en una aplicación que involucra Struts 2 y Servlets para manejar diferentes tipos de tareas en hilos separados, mejorando la capacidad de respuesta de la aplicación.

### Escenario:

Imagina una aplicación web donde:

- **Hilo 1 (Struts 2)**: Maneja las peticiones del usuario, ejecuta la lógica de la aplicación a través de las acciones de Struts 2 y genera la respuesta (por ejemplo, renderizar una página JSP).
- **Hilo 2 (Servlet)**: Realiza una tarea en segundo plano que no necesita bloquear la respuesta al usuario, como el procesamiento de datos, el envío de correos electrónicos o la actualización de un caché.

### Implementación:

1. **Tarea en Segundo Plano (Servlet)**: Puedes crear un Servlet que inicie un nuevo hilo (usando `Thread` o `ExecutorService`) para realizar la tarea en segundo plano cuando se recibe una petición específica.

```java
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/tareaEnSegundoPlano")
public class TareaEnSegundoPlanoServlet extends HttpServlet {
    private ExecutorService executor = Executors.newFixedThreadPool(2); // Pool para tareas en segundo plano

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Tarea en segundo plano iniciada por el Servlet.");
                // Simula una tarea larga
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Tarea en segundo plano completada.");
                // Puedes realizar aquí operaciones como enviar un correo,
                // actualizar la base de datos, etc.
            }
        });

        response.getWriter().println("Petición recibida. La tarea en segundo plano se ha iniciado.");
    }

    @Override
    public void destroy() {
        executor.shutdownNow(); // Intenta detener las tareas en ejecución al destruir el Servlet
    }
}
```

2. **Procesamiento Principal (Struts 2)**: Las acciones de Struts 2 se ejecutan en los hilos gestionados por el contenedor web. Su objetivo principal es manejar la lógica de la aplicación basada en la entrada del usuario y preparar los datos para la vista. No deberían bloquearse esperando tareas largas en segundo plano si la respuesta al usuario no depende directamente de ellas.

```java
// Ejemplo de una Acción de Struts 2
public class MiAccion extends ActionSupport {
    private String mensaje;

    public String execute() throws Exception {
        mensaje = "Datos procesados por Struts 2.";
        // Puedes iniciar una tarea en segundo plano desde aquí
        // si es necesario, pero generalmente se delega a un Servlet
        return SUCCESS;
    }

    public String getMensaje() {
        return mensaje;
    }
}
```

### Comunicación (si es necesaria):

Si el hilo del Servlet necesita comunicar resultados al hilo de Struts 2 (o viceversa), puedes utilizar mecanismos como:

- **Variables compartidas con control de concurrencia**: Usar `synchronized` o clases del `java.util.concurrent` package (como `BlockingQueue` para pasar datos de forma segura entre hilos).
- **Almacenamiento compartido**: Escribir y leer datos de una base de datos o un caché compartido.

### Consideraciones:

- **Gestión del ciclo de vida de los hilos**: Asegúrate de gestionar correctamente el ciclo de vida de los hilos creados, especialmente en el contexto de un contenedor web (por ejemplo, al destruir un Servlet, detén los `ExecutorService`).
- **Sincronización adecuada**: Utiliza mecanismos de sincronización solo cuando sea necesario para proteger los recursos compartidos y evitar problemas de concurrencia. El uso excesivo de `synchronized` puede llevar a la contención y reducir el rendimiento.
- **Pools de hilos**: Generalmente, es mejor utilizar `ExecutorService` para gestionar hilos en lugar de crear y destruir hilos individuales, ya que los pools de hilos son más eficientes.
En resumen, puedes mantener la lógica principal de tu aplicación web en los hilos gestionados por Struts 2 para las peticiones de usuario, y delegar tareas asíncronas o en segundo plano a Servlets que gestionen sus propios hilos (a través de `Thread` o `ExecutorService`) para evitar bloquear la respuesta al usuario. La comunicación entre estos hilos debe realizarse de forma segura utilizando mecanismos de concurrencia apropiados.
