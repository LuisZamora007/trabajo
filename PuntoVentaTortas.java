package poo_punto_de_venta;
import java.util.*;
import java.io.*;


// Ingredientes

class Ingrediente {
    private final String nombre;
    private final double precio;

    public Ingrediente(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
}


// Productos
abstract class Producto {
    protected int id;
    protected String nombre;
    protected double precio;

    public Producto(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }

    public abstract String getTipo();
    
    @Override
    public String toString() {
        return id + " - " + nombre + " ($" + precio + ")";
    }
}

// Torta avanzada
class Torta extends Producto {
    private final List<Ingrediente> extras;
    private final List<String> sin;

    public Torta(int id, String nombre, double precio) {
        super(id, nombre, precio);
        extras = new ArrayList<>();
        sin = new ArrayList<>();
    }

    public void agregarExtra(Ingrediente ing) {
        extras.add(ing);
    }

    public void quitarIngrediente(String nombre) {
        sin.add(nombre);
    }
    @Override
    public double getPrecio() {
        double total = precio;
        for (int i = 0; i < extras.size(); i++) {
            total += extras.get(i).getPrecio();
        }
        return total;
    }
    @Override
    public String getTipo() { return "Torta"; }
    
    
    @Override
    public String getNombre() {
        String desc = nombre;

        if (sin.size() > 0) {
            desc += " (sin ";
            for (int i = 0; i < sin.size(); i++) {
                desc += sin.get(i);
                if (i < sin.size() - 1) desc += ", ";
            }
            desc += ")";
        }

        if (extras.size() > 0) {
            desc += " + ";
            for (int i = 0; i < extras.size(); i++) {
                desc += extras.get(i).getNombre();
                if (i < extras.size() - 1) desc += ", ";
            }
        }

        return desc;
    }
}

// Bebidas

class Bebida extends Producto {
    private final String tamano;

    public Bebida(int id, String nombre, double precio, String tamano) {
        super(id, nombre, precio);
        this.tamano = tamano;
    }

    @Override
    public String getTipo() { return "Bebida"; }
}

// Cliente

class Cliente {
    private final String nombre;

    public Cliente(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
}

// ItemVenta

class ItemVenta {
    private final Producto producto;
    private final int cantidad;

    public ItemVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    public String getDescripcion() {
        return producto.getNombre() + " x" + cantidad + " = $" + getSubtotal();
    }
}

// Venta
 
class Venta {
    private final int id;
    private final Cliente cliente;
    private final List<ItemVenta> items;

    public Venta(int id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.items = new ArrayList<>();
    }

    public void agregarItem(ItemVenta item) {
        items.add(item);
    }

    public double calcularTotal() {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getSubtotal();
        }
        return total;
    }

    public String generarTicket(double pago, double cambio) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== TORTAS DE LA BARDA =====\n");
        sb.append("Cliente: ").append(cliente.getNombre()).append("\n");

        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i).getDescripcion()).append("\n");
        }

        sb.append("TOTAL: $").append(calcularTotal()).append("\n");
        sb.append("PAGO: $").append(pago).append("\n");
        sb.append("CAMBIO: $").append(cambio).append("\n");
        sb.append("=============================\n");

        return sb.toString();
    }
}

// Inventario

class Inventario {
    private final List<Producto> productos;

    public Inventario() {
        productos = new ArrayList<>();
    }

    public void agregarProducto(Producto p) {
        productos.add(p);
    }

    public Producto buscar(int id) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId() == id) return productos.get(i);
        }
        return null;
    }

    public void mostrarMenu() {
        System.out.println("\n--- MENU ---");
        for (int i = 0; i < productos.size(); i++) {
            System.out.println(productos.get(i));
        }
    }
}

// Archivo

class Archivo {
    public static void guardarTicket(String contenido) {
        try {
            try (FileWriter fw = new FileWriter("ventas.txt", true)) {
                fw.write(contenido + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error al guardar archivo");
        }
    }
}

// Caja

class Caja {
    private double dineroTotal = 0;

    public void registrarVenta(double total) {
        dineroTotal += total;
    }

    public double getDineroTotal() {
        return dineroTotal;
    }
}

// Clase principal

public class PuntoVentaTortas {

    static Scanner sc = new Scanner(System.in);
    static Inventario inventario = new Inventario();
    static Caja caja = new Caja();
    static List<Ingrediente> listaIngredientes = new ArrayList<Ingrediente>();

    public static void main(String[] args) {
        cargarMenu();
        cargarIngredientes();
        menu();
    }

    public static void cargarIngredientes() {
        listaIngredientes.add(new Ingrediente("Jamon", 10));
        listaIngredientes.add(new Ingrediente("Queso", 12));
        listaIngredientes.add(new Ingrediente("Aguacate", 15));
        listaIngredientes.add(new Ingrediente("Chorizo", 8));
        listaIngredientes.add(new Ingrediente("Salsa extra", 10));
    }

    public static void cargarMenu() {
        inventario.agregarProducto(new Torta(1, "Torta Base", 50));
        inventario.agregarProducto(new Bebida(2, "Refresco", 25, "600ml"));
        inventario.agregarProducto(new Bebida(3, "Agua", 20, "500ml"));
    }

    public static void menu() {
        int op;

        do {
            System.out.println("\n1. Ver menú");
            System.out.println("2. Nueva venta");
            System.out.println("3. Ver caja");
            System.out.println("0. Salir");

            op = sc.nextInt();

            switch (op) {
                case 1:
                    inventario.mostrarMenu();
                    break;
                case 2:
                    venta();
                    break;
                case 3:
                    System.out.println("Caja: $" + caja.getDineroTotal());
                    break;
            }

        } while (op != 0);
    }

    public static void venta() {
        Venta venta = new Venta(new Random().nextInt(1000), new Cliente("General"));

        int id;

        do {
            inventario.mostrarMenu();
            System.out.print("Producto (0 salir): ");
            id = sc.nextInt();

            if (id != 0) {
                Producto p = inventario.buscar(id);

                if (p != null) {

                    if (p instanceof Torta) {
                        Torta t = new Torta(p.getId(), p.getNombre(), p.getPrecio());

                        int opcion;
                        do {
                            System.out.println("\n--- PERSONALIZAR TORTA ---");
                            System.out.println("1. Quitar ingredientes");
                            System.out.println("2. Agregar extras");
                            System.out.println("0. Terminar");

                            opcion = sc.nextInt();

                            switch (opcion) {
                                case 1:
                                    System.out.println("1. Jamon\n2. Queso\n3. Frijol\n4. Lechuga");
                                    int q = sc.nextInt();
                                    if (q == 1) t.quitarIngrediente("Jamon");
                                    if (q == 2) t.quitarIngrediente("Queso");
                                    if (q == 3) t.quitarIngrediente("Frijol");
                                    if (q == 4) t.quitarIngrediente("Lechuga");
                                    break;

                                case 2:
                                    int opc;
                                    do {
                                        System.out.println("\nExtras:");
                                        for (int i = 0; i < listaIngredientes.size(); i++) {
                                            System.out.println((i + 1) + ". " + listaIngredientes.get(i).getNombre() +
                                                    " ($" + listaIngredientes.get(i).getPrecio() + ")");
                                        }
                                        System.out.println("0. Terminar");

                                        opc = sc.nextInt();

                                        if (opc > 0 && opc <= listaIngredientes.size()) {
                                            t.agregarExtra(listaIngredientes.get(opc - 1));
                                        }

                                    } while (opc != 0);
                                    break;
                            }

                        } while (opcion != 0);

                        System.out.print("Cantidad: ");
                        int cant = sc.nextInt();
                        venta.agregarItem(new ItemVenta(t, cant));

                    } else {
                        System.out.print("Cantidad: ");
                        int cant = sc.nextInt();
                        venta.agregarItem(new ItemVenta(p, cant));
                    }

                } else {
                    System.out.println("Producto no encontrado");
                }
            }

        } while (id != 0);

        double total = venta.calcularTotal();
        System.out.println("Total: $" + total);

        double pago;
        do {
            System.out.print("Pago: ");
            pago = sc.nextDouble();
        } while (pago < total);

        double cambio = pago - total;

        caja.registrarVenta(total);

        String ticket = venta.generarTicket(pago, cambio);
        System.out.println(ticket);

        Archivo.guardarTicket(ticket);
    }
}




