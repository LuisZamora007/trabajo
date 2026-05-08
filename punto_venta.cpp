#include <iostream>
#include <vector>
#include <string>
#include <fstream>
#include <sstream>
#include <memory>
#include <cstdlib>

using namespace std;

// Ingrediente
class Ingrediente {
public:
    string nombre;
    double precio;

    Ingrediente(string nombre, double precio) {
        this->nombre = nombre;
        this->precio = precio;
    }
};

// Producto (abstracto)
class Producto {
protected:
    int id;
    string nombre;
    double precio;

public:
    Producto(int id, string nombre, double precio) {
        this->id = id;
        this->nombre = nombre;
        this->precio = precio;
    }

    virtual double getPrecio() {
        return precio;
    }

    int getId() { return id; }
    string getNombre() { return nombre; }

    virtual string getTipo() = 0;

    virtual string toString() {
        stringstream ss;
        ss << id << " - " << nombre << " ($" << getPrecio() << ")";
        return ss.str();
    }

    virtual ~Producto() {}
};

// Torta
class Torta : public Producto {
private:
    vector<Ingrediente> extras;
    vector<string> sin;

public:
    Torta(int id, string nombre, double precio)
        : Producto(id, nombre, precio) {}

    void agregarExtra(Ingrediente ing) {
        extras.push_back(ing);
    }

    void quitarIngrediente(string nombre) {
        sin.push_back(nombre);
    }

    double getPrecio() override {
        double total = precio;
        for (auto& e : extras) {
            total += e.precio;
        }
        return total;
    }

    string getTipo() override {
        return "Torta";
    }

    string getNombreModificado() {
        string desc = nombre;

        if (!sin.empty()) {
            desc += " (sin ";
            for (size_t i = 0; i < sin.size(); i++) {
                desc += sin[i];
                if (i < sin.size() - 1) desc += ", ";
            }
            desc += ")";
        }

        if (!extras.empty()) {
            desc += " + ";
            for (size_t i = 0; i < extras.size(); i++) {
                desc += extras[i].nombre;
                if (i < extras.size() - 1) desc += ", ";
            }
        }

        return desc;
    }

    string toString() override {
        stringstream ss;
        ss << id << " - " << getNombreModificado() << " ($" << getPrecio() << ")";
        return ss.str();
    }
};

// Bebida
class Bebida : public Producto {
private:
    string tamano;

public:
    Bebida(int id, string nombre, double precio, string tamano)
        : Producto(id, nombre, precio) {
        this->tamano = tamano;
    }

    string getTipo() override {
        return "Bebida";
    }
};

// Cliente
class Cliente {
public:
    string nombre;

    Cliente(string nombre) {
        this->nombre = nombre;
    }
};

// ItemVenta
class ItemVenta {
private:
    shared_ptr<Producto> producto;
    int cantidad;

public:
    ItemVenta(shared_ptr<Producto> producto, int cantidad) {
        this->producto = producto;
        this->cantidad = cantidad;
    }

    double getSubtotal() {
        return producto->getPrecio() * cantidad;
    }

    string getDescripcion() {
        stringstream ss;
        ss << producto->toString() << " x" << cantidad << " = $" << getSubtotal();
        return ss.str();
    }
};

// Venta
class Venta {
private:
    int id;
    Cliente cliente;
    vector<ItemVenta> items;

public:
    Venta(int id, Cliente cliente) : id(id), cliente(cliente) {}

    void agregarItem(ItemVenta item) {
        items.push_back(item);
    }

    double calcularTotal() {
        double total = 0;
        for (auto& i : items) {
            total += i.getSubtotal();
        }
        return total;
    }

    string generarTicket(double pago, double cambio) {
        stringstream ss;
        ss << "===== TORTAS DE LA BARDA =====\n";
        ss << "Cliente: " << cliente.nombre << "\n";

        for (auto& i : items) {
            ss << i.getDescripcion() << "\n";
        }

        ss << "TOTAL: $" << calcularTotal() << "\n";
        ss << "PAGO: $" << pago << "\n";
        ss << "CAMBIO: $" << cambio << "\n";
        ss << "=============================\n";

        return ss.str();
    }
};

// Inventario
class Inventario {
private:
    vector<shared_ptr<Producto>> productos;

public:
    void agregarProducto(shared_ptr<Producto> p) {
        productos.push_back(p);
    }

    shared_ptr<Producto> buscar(int id) {
        for (auto& p : productos) {
            if (p->getId() == id) return p;
        }
        return nullptr;
    }

    void mostrarMenu() {
        cout << "\n--- MENU ---\n";
        for (auto& p : productos) {
            cout << p->toString() << endl;
        }
    }
};

// Archivo
class Archivo {
public:
    static void guardarTicket(string contenido) {
        ofstream file("ventas.txt", ios::app);
        if (file.is_open()) {
            file << contenido << endl;
            file.close();
        } else {
            cout << "Error al guardar archivo\n";
        }
    }
};

// Caja
class Caja {
private:
    double dineroTotal = 0;

public:
    void registrarVenta(double total) {
        dineroTotal += total;
    }

    double getDineroTotal() {
        return dineroTotal;
    }
};

// Variables globales
Inventario inventario;
Caja caja;
vector<Ingrediente> listaIngredientes;

// Funciones
void cargarIngredientes() {
    listaIngredientes.push_back(Ingrediente("Jamon", 10));
    listaIngredientes.push_back(Ingrediente("Queso", 12));
    listaIngredientes.push_back(Ingrediente("Aguacate", 15));
    listaIngredientes.push_back(Ingrediente("Chorizo", 8));
    listaIngredientes.push_back(Ingrediente("Salsa extra", 10));
}

void cargarMenu() {
    inventario.agregarProducto(make_shared<Torta>(1, "Torta Base", 50));
    inventario.agregarProducto(make_shared<Bebida>(2, "Refresco", 25, "600ml"));
    inventario.agregarProducto(make_shared<Bebida>(3, "Agua", 20, "500ml"));
}

void venta() {
    Venta v(rand() % 1000, Cliente("General"));

    int id;
    do {
        inventario.mostrarMenu();
        cout << "Producto (0 salir): ";
        cin >> id;

        if (id != 0) {
            auto p = inventario.buscar(id);

            if (p != nullptr) {

                if (p->getTipo() == "Torta") {
                    auto t = make_shared<Torta>(p->getId(), p->getNombre(), p->getPrecio());

                    int opcion;
                    do {
                        cout << "\n1. Quitar ingredientes\n2. Agregar extras\n0. Terminar\n";
                        cin >> opcion;

                        if (opcion == 1) {
                            cout << "1. Jamon\n2. Queso\n3. Frijol\n4. Lechuga\n";
                            int q; cin >> q;

                            if (q == 1) t->quitarIngrediente("Jamon");
                            if (q == 2) t->quitarIngrediente("Queso");
                            if (q == 3) t->quitarIngrediente("Frijol");
                            if (q == 4) t->quitarIngrediente("Lechuga");
                        }
                        else if (opcion == 2) {
                            int opc;
                            do {
                                cout << "\nExtras:\n";
                                for (size_t i = 0; i < listaIngredientes.size(); i++) {
                                    cout << i + 1 << ". " << listaIngredientes[i].nombre
                                         << " ($" << listaIngredientes[i].precio << ")\n";
                                }
                                cout << "0. Terminar\n";

                                cin >> opc;

                                if (opc > 0 && opc <= listaIngredientes.size()) {
                                    t->agregarExtra(listaIngredientes[opc - 1]);
                                }

                            } while (opc != 0);
                        }

                    } while (opcion != 0);

                    int cant;
                    cout << "Cantidad: ";
                    cin >> cant;
                    v.agregarItem(ItemVenta(t, cant));

                } else {
                    int cant;
                    cout << "Cantidad: ";
                    cin >> cant;
                    v.agregarItem(ItemVenta(p, cant));
                }

            } else {
                cout << "Producto no encontrado\n";
            }
        }

    } while (id != 0);

    double total = v.calcularTotal();
    cout << "Total: $" << total << endl;

    double pago;
    do {
        cout << "Pago: ";
        cin >> pago;
    } while (pago < total);

    double cambio = pago - total;

    caja.registrarVenta(total);

    string ticket = v.generarTicket(pago, cambio);
    cout << ticket;

    Archivo::guardarTicket(ticket);
}

void menu() {
    int op;
    do {
        cout << "\n1. Ver menú\n2. Nueva venta\n3. Ver caja\n0. Salir\n";
        cin >> op;

        switch (op) {
            case 1: inventario.mostrarMenu(); break;
            case 2: venta(); break;
            case 3: cout << "Caja: $" << caja.getDineroTotal() << endl; break;
        }

    } while (op != 0);
}

// MAIN
int main() {
    cargarMenu();
    cargarIngredientes();
    menu();
    return 0;
}
