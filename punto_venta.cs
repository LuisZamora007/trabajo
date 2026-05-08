using System;
using System.Collections.Generic;
using System.IO;

// Ingrediente
class Ingrediente
{
    public string Nombre { get; }
    public double Precio { get; }

    public Ingrediente(string nombre, double precio)
    {
        Nombre = nombre;
        Precio = precio;
    }
}

// Producto (abstracto)
abstract class Producto
{
    public int Id { get; protected set; }
    public string Nombre { get; protected set; }
    protected double precio;

    public Producto(int id, string nombre, double precio)
    {
        Id = id;
        Nombre = nombre;
        this.precio = precio;
    }

    public virtual double GetPrecio()
    {
        return precio;
    }

    public abstract string GetTipo();

    public override string ToString()
    {
        return $"{Id} - {Nombre} (${GetPrecio()})";
    }
}

// Torta
class Torta : Producto
{
    private List<Ingrediente> extras = new List<Ingrediente>();
    private List<string> sin = new List<string>();

    public Torta(int id, string nombre, double precio) : base(id, nombre, precio) { }

    public void AgregarExtra(Ingrediente ing)
    {
        extras.Add(ing);
    }

    public void QuitarIngrediente(string nombre)
    {
        sin.Add(nombre);
    }

    public override double GetPrecio()
    {
        double total = precio;
        foreach (var e in extras)
        {
            total += e.Precio;
        }
        return total;
    }

    public override string GetTipo()
    {
        return "Torta";
    }

    public override string ToString()
    {
        string desc = Nombre;

        if (sin.Count > 0)
        {
            desc += " (sin " + string.Join(", ", sin) + ")";
        }

        if (extras.Count > 0)
        {
            List<string> nombres = new List<string>();
            foreach (var e in extras)
                nombres.Add(e.Nombre);

            desc += " + " + string.Join(", ", nombres);
        }

        return $"{Id} - {desc} (${GetPrecio()})";
    }
}

// Bebida
class Bebida : Producto
{
    private string tamano;

    public Bebida(int id, string nombre, double precio, string tamano)
        : base(id, nombre, precio)
    {
        this.tamano = tamano;
    }

    public override string GetTipo()
    {
        return "Bebida";
    }
}

// Cliente
class Cliente
{
    public string Nombre { get; }

    public Cliente(string nombre)
    {
        Nombre = nombre;
    }
}

// ItemVenta
class ItemVenta
{
    private Producto producto;
    private int cantidad;

    public ItemVenta(Producto producto, int cantidad)
    {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public double GetSubtotal()
    {
        return producto.GetPrecio() * cantidad;
    }

    public string GetDescripcion()
    {
        return $"{producto.ToString()} x{cantidad} = ${GetSubtotal()}";
    }
}

// Venta
class Venta
{
    private int id;
    private Cliente cliente;
    private List<ItemVenta> items = new List<ItemVenta>();

    public Venta(int id, Cliente cliente)
    {
        this.id = id;
        this.cliente = cliente;
    }

    public void AgregarItem(ItemVenta item)
    {
        items.Add(item);
    }

    public double CalcularTotal()
    {
        double total = 0;
        foreach (var i in items)
        {
            total += i.GetSubtotal();
        }
        return total;
    }

    public string GenerarTicket(double pago, double cambio)
    {
        string ticket = "===== TORTAS DE LA BARDA =====\n";
        ticket += "Cliente: " + cliente.Nombre + "\n";

        foreach (var i in items)
        {
            ticket += i.GetDescripcion() + "\n";
        }

        ticket += "TOTAL: $" + CalcularTotal() + "\n";
        ticket += "PAGO: $" + pago + "\n";
        ticket += "CAMBIO: $" + cambio + "\n";
        ticket += "=============================\n";

        return ticket;
    }
}

// Inventario
class Inventario
{
    private List<Producto> productos = new List<Producto>();

    public void AgregarProducto(Producto p)
    {
        productos.Add(p);
    }

    public Producto Buscar(int id)
    {
        foreach (var p in productos)
        {
            if (p.Id == id) return p;
        }
        return null;
    }

    public void MostrarMenu()
    {
        Console.WriteLine("\n--- MENU ---");
        foreach (var p in productos)
        {
            Console.WriteLine(p);
        }
    }
}

// Archivo
class Archivo
{
    public static void GuardarTicket(string contenido)
    {
        try
        {
            File.AppendAllText("ventas.txt", contenido + "\n");
        }
        catch
        {
            Console.WriteLine("Error al guardar archivo");
        }
    }
}

// Caja
class Caja
{
    private double dineroTotal = 0;

    public void RegistrarVenta(double total)
    {
        dineroTotal += total;
    }

    public double GetDineroTotal()
    {
        return dineroTotal;
    }
}

// Programa principal
class Program
{
    static Inventario inventario = new Inventario();
    static Caja caja = new Caja();
    static List<Ingrediente> listaIngredientes = new List<Ingrediente>();

    static void Main()
    {
        CargarMenu();
        CargarIngredientes();
        Menu();
    }

    static void CargarIngredientes()
    {
        listaIngredientes.Add(new Ingrediente("Jamon", 10));
        listaIngredientes.Add(new Ingrediente("Queso", 12));
        listaIngredientes.Add(new Ingrediente("Aguacate", 15));
        listaIngredientes.Add(new Ingrediente("chorizo", 8));
        listaIngredientes.Add(new Ingrediente("Salsa extra", 10));
    }

    static void CargarMenu()
    {
        inventario.AgregarProducto(new Torta(1, "Torta Base", 50));
        inventario.AgregarProducto(new Bebida(2, "Refresco", 25, "600ml"));
        inventario.AgregarProducto(new Bebida(3, "Agua", 20, "500ml"));
    }

    static void Menu()
    {
        int op;

        do
        {
            Console.WriteLine("\n1. Ver menú");
            Console.WriteLine("2. Nueva venta");
            Console.WriteLine("3. Ver caja");
            Console.WriteLine("0. Salir");

            op = int.Parse(Console.ReadLine());

            switch (op)
            {
                case 1:
                    inventario.MostrarMenu();
                    break;
                case 2:
                    VentaProceso();
                    break;
                case 3:
                    Console.WriteLine("Caja: $" + caja.GetDineroTotal());
                    break;
            }

        } while (op != 0);
    }

    static void VentaProceso()
    {
        Venta venta = new Venta(new Random().Next(1000), new Cliente("General"));

        int id;

        do
        {
            inventario.MostrarMenu();
            Console.Write("Producto (0 salir): ");
            id = int.Parse(Console.ReadLine());

            if (id != 0)
            {
                Producto p = inventario.Buscar(id);

                if (p != null)
                {
                    if (p is Torta)
                    {
                        Torta t = new Torta(p.Id, p.Nombre, p.GetPrecio());

                        int opcion;
                        do
                        {
                            Console.WriteLine("\n1. Quitar ingredientes");
                            Console.WriteLine("2. Agregar extras");
                            Console.WriteLine("0. Terminar");

                            opcion = int.Parse(Console.ReadLine());

                            if (opcion == 1)
                            {
                                Console.WriteLine("1. Jamon\n2. Queso\n3. Frijol\n4. Lechuga");
                                int q = int.Parse(Console.ReadLine());

                                if (q == 1) t.QuitarIngrediente("Jamon");
                                if (q == 2) t.QuitarIngrediente("Queso");
                                if (q == 3) t.QuitarIngrediente("Frijol");
                                if (q == 4) t.QuitarIngrediente("Lechuga");
                            }
                            else if (opcion == 2)
                            {
                                int opc;
                                do
                                {
                                    Console.WriteLine("\nExtras:");
                                    for (int i = 0; i < listaIngredientes.Count; i++)
                                    {
                                        Console.WriteLine($"{i + 1}. {listaIngredientes[i].Nombre} (${listaIngredientes[i].Precio})");
                                    }
                                    Console.WriteLine("0. Terminar");

                                    opc = int.Parse(Console.ReadLine());

                                    if (opc > 0 && opc <= listaIngredientes.Count)
                                    {
                                        t.AgregarExtra(listaIngredientes[opc - 1]);
                                    }

                                } while (opc != 0);
                            }

                        } while (opcion != 0);

                        Console.Write("Cantidad: ");
                        int cant = int.Parse(Console.ReadLine());
                        venta.AgregarItem(new ItemVenta(t, cant));
                    }
                    else
                    {
                        Console.Write("Cantidad: ");
                        int cant = int.Parse(Console.ReadLine());
                        venta.AgregarItem(new ItemVenta(p, cant));
                    }
                }
                else
                {
                    Console.WriteLine("Producto no encontrado");
                }
            }

        } while (id != 0);

        double total = venta.CalcularTotal();
        Console.WriteLine("Total: $" + total);

        double pago;
        do
        {
            Console.Write("Pago: ");
            pago = double.Parse(Console.ReadLine());
        } while (pago < total);

        double cambio = pago - total;

        caja.RegistrarVenta(total);

        string ticket = venta.GenerarTicket(pago, cambio);
        Console.WriteLine(ticket);

        Archivo.GuardarTicket(ticket);
    }
}