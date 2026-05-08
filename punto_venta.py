import random

# Ingrediente
class Ingrediente:
    def __init__(self, nombre, precio):
        self.nombre = nombre
        self.precio = precio


# Producto (abstracto)
class Producto:
    def __init__(self, id, nombre, precio):
        self.id = id
        self.nombre = nombre
        self.precio = precio

    def get_precio(self):
        return self.precio

    def get_tipo(self):
        raise NotImplementedError

    def __str__(self):
        return f"{self.id} - {self.nombre} (${self.get_precio()})"


# Torta
class Torta(Producto):
    def __init__(self, id, nombre, precio):
        super().__init__(id, nombre, precio)
        self.extras = []
        self.sin = []

    def agregar_extra(self, ing):
        self.extras.append(ing)

    def quitar_ingrediente(self, nombre):
        self.sin.append(nombre)

    def get_precio(self):
        total = self.precio
        for e in self.extras:
            total += e.precio
        return total

    def get_tipo(self):
        return "Torta"

    def __str__(self):
        desc = self.nombre

        if self.sin:
            desc += " (sin " + ", ".join(self.sin) + ")"

        if self.extras:
            desc += " + " + ", ".join([e.nombre for e in self.extras])

        return f"{self.id} - {desc} (${self.get_precio()})"


# Bebida
class Bebida(Producto):
    def __init__(self, id, nombre, precio, tamano):
        super().__init__(id, nombre, precio)
        self.tamano = tamano

    def get_tipo(self):
        return "Bebida"


# Cliente
class Cliente:
    def __init__(self, nombre):
        self.nombre = nombre


# ItemVenta
class ItemVenta:
    def __init__(self, producto, cantidad):
        self.producto = producto
        self.cantidad = cantidad

    def get_subtotal(self):
        return self.producto.get_precio() * self.cantidad

    def get_descripcion(self):
        return f"{self.producto} x{self.cantidad} = ${self.get_subtotal()}"


# Venta
class Venta:
    def __init__(self, id, cliente):
        self.id = id
        self.cliente = cliente
        self.items = []

    def agregar_item(self, item):
        self.items.append(item)

    def calcular_total(self):
        return sum(i.get_subtotal() for i in self.items)

    def generar_ticket(self, pago, cambio):
        ticket = "===== TORTAS DE LA BARDA =====\n"
        ticket += f"Cliente: {self.cliente.nombre}\n"

        for i in self.items:
            ticket += i.get_descripcion() + "\n"

        ticket += f"TOTAL: ${self.calcular_total()}\n"
        ticket += f"PAGO: ${pago}\n"
        ticket += f"CAMBIO: ${cambio}\n"
        ticket += "=============================\n"

        return ticket


# Inventario
class Inventario:
    def __init__(self):
        self.productos = []

    def agregar_producto(self, p):
        self.productos.append(p)

    def buscar(self, id):
        for p in self.productos:
            if p.id == id:
                return p
        return None

    def mostrar_menu(self):
        print("\n--- MENU ---")
        for p in self.productos:
            print(p)


# Archivo
class Archivo:
    @staticmethod
    def guardar_ticket(contenido):
        try:
            with open("ventas.txt", "a") as f:
                f.write(contenido + "\n")
        except:
            print("Error al guardar archivo")


# Caja
class Caja:
    def __init__(self):
        self.dinero_total = 0

    def registrar_venta(self, total):
        self.dinero_total += total

    def get_dinero_total(self):
        return self.dinero_total


# Programa principal
inventario = Inventario()
caja = Caja()
lista_ingredientes = []


def cargar_ingredientes():
    lista_ingredientes.append(Ingrediente("Jamon", 10))
    lista_ingredientes.append(Ingrediente("Queso", 12))
    lista_ingredientes.append(Ingrediente("Aguacate", 15))
    lista_ingredientes.append(Ingrediente("Chorizo", 8))
    lista_ingredientes.append(Ingrediente("Salsa extra", 10))


def cargar_menu():
    inventario.agregar_producto(Torta(1, "Torta Base", 50))
    inventario.agregar_producto(Bebida(2, "Refresco", 25, "600ml"))
    inventario.agregar_producto(Bebida(3, "Agua", 20, "500ml"))


def menu():
    while True:
        print("\n1. Ver menú")
        print("2. Nueva venta")
        print("3. Ver caja")
        print("0. Salir")

        op = int(input())

        if op == 1:
            inventario.mostrar_menu()
        elif op == 2:
            venta()
        elif op == 3:
            print(f"Caja: ${caja.get_dinero_total()}")
        elif op == 0:
            break


def venta():
    venta = Venta(random.randint(1, 1000), Cliente("General"))

    while True:
        inventario.mostrar_menu()
        id = int(input("Producto (0 salir): "))

        if id == 0:
            break

        p = inventario.buscar(id)

        if p:
            if isinstance(p, Torta):
                t = Torta(p.id, p.nombre, p.precio)

                while True:
                    print("\n1. Quitar ingredientes")
                    print("2. Agregar extras")
                    print("0. Terminar")

                    opcion = int(input())

                    if opcion == 1:
                        print("1. Jamon\n2. Queso\n3. Frijol\n4. Lechuga")
                        q = int(input())

                        if q == 1: t.quitar_ingrediente("Jamon")
                        if q == 2: t.quitar_ingrediente("Queso")
                        if q == 3: t.quitar_ingrediente("Frijol")
                        if q == 4: t.quitar_ingrediente("Lechuga")

                    elif opcion == 2:
                        while True:
                            print("\nExtras:")
                            for i, ing in enumerate(lista_ingredientes):
                                print(f"{i+1}. {ing.nombre} (${ing.precio})")
                            print("0. Terminar")

                            opc = int(input())

                            if opc == 0:
                                break

                            if 1 <= opc <= len(lista_ingredientes):
                                t.agregar_extra(lista_ingredientes[opc-1])

                    elif opcion == 0:
                        break

                cant = int(input("Cantidad: "))
                venta.agregar_item(ItemVenta(t, cant))

            else:
                cant = int(input("Cantidad: "))
                venta.agregar_item(ItemVenta(p, cant))
        else:
            print("Producto no encontrado")

    total = venta.calcular_total()
    print(f"Total: ${total}")

    while True:
        pago = float(input("Pago: "))
        if pago >= total:
            break

    cambio = pago - total

    caja.registrar_venta(total)

    ticket = venta.generar_ticket(pago, cambio)
    print(ticket)

    Archivo.guardar_ticket(ticket)


# Ejecutar
cargar_menu()
cargar_ingredientes()
menu()
