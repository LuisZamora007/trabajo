import java.util.Scanner;

class Concepto {
    String codigo;
    String insumo;
    String descripcion;
    String unidadMedida;

    public Concepto(String codigo, String insumo, String descripcion, String unidadMedida) {
        this.codigo = codigo;
        this.insumo = insumo;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
    }
}

class Catalogo {
    Concepto[] materiales;
    Concepto[] manoDeObra;
    Concepto[] maquinariaYEquipo;
    Concepto[] servicios;

    int posMateriales;
    int posManoDeObra;
    int posMaquinariaYEquipo;
    int posServicios;

    public Catalogo(int tam) {
        materiales = new Concepto[tam];
        manoDeObra = new Concepto[tam];
        maquinariaYEquipo = new Concepto[tam];
        servicios = new Concepto[tam];

        posMateriales = -1;
        posManoDeObra = -1;
        posMaquinariaYEquipo = -1;
        posServicios = -1;
    }
}

public class practica_2 {
    static Catalogo catalogo;
    static int tam = 100;
    static Scanner sc = new Scanner(System.in);

    public static String dialogo(String texto) {
        System.out.print(texto + ": ");
        return sc.nextLine();
    }

    public static String mostrarMenu(String[] opciones) {
        StringBuilder cadena = new StringBuilder();
        for (String opcion : opciones) {
            cadena.append(opcion).append("\n");
        }
        cadena.append("Que opcion deseas: ");
        return cadena.toString();
    }

    public static String desplegarMenu(String titulo, String[] menu) {
        System.out.println("\n" + titulo);
        return dialogo(mostrarMenu(menu));
    }

    public static void crearCatalogo() {
        catalogo = new Catalogo(tam);
    }

    public static boolean existe(String codigo) {
        return buscarConcepto(codigo) != null;
    }

    public static void altasConceptos() {
        String codigo = dialogo("Introduce el codigo").trim();

        if (existe(codigo)) {
            System.out.println("El codigo ya existe.");
            return;
        }

        String insumo = dialogo("Introduce el nombre del insumo");
        String descripcion = dialogo("Introduce la descripcion");
        String unidadMedida = dialogo("Introduce la unidad de medida");

        Concepto nuevo = new Concepto(codigo, insumo, descripcion, unidadMedida);

        int segmento = Integer.parseInt(dialogo("1.Material\n2.Mano de Obra\n3.Maquinaria\n4.Servicios"));

        insertarInsumo(segmento, nuevo);
    }

    private static void insertarInsumo(int segmento, Concepto nodo) {
        Catalogo c = catalogo;
        switch (segmento) {
            case 1 -> {
                if (c.posMateriales < tam - 1) {
                    c.posMateriales++;
                    c.materiales[c.posMateriales] = nodo;
                    System.out.println("Material agregado.");
                } else {
                    System.out.println("No hay espacio en Materiales.");
                }
            }
            case 2 -> {
                if (c.posManoDeObra < tam - 1) {
                    c.posManoDeObra++;
                    c.manoDeObra[c.posManoDeObra] = nodo;
                    System.out.println("Mano de Obra agregada.");
                } else {
                    System.out.println("No hay espacio en Mano de Obra.");
                }
            }
            case 3 -> {
                if (c.posMaquinariaYEquipo < tam - 1) {
                    c.posMaquinariaYEquipo++;
                    c.maquinariaYEquipo[c.posMaquinariaYEquipo] = nodo;
                    System.out.println("Maquinaria y Equipo agregado.");
                } else {
                    System.out.println("No hay espacio en Maquinaria y Equipo.");
                }
            }
            case 4 -> {
                if (c.posServicios < tam - 1) {
                    c.posServicios++;
                    c.servicios[c.posServicios] = nodo;
                    System.out.println("Servicio agregado.");
                } else {
                    System.out.println("No hay espacio en Servicios.");
                }
            }
            default -> System.out.println("Segmento inválido.");
        }
    }

    private static Concepto buscarConcepto(String codigo) {
        Catalogo c = catalogo;
        for (int i = 0; i <= c.posMateriales; i++) {
            if (c.materiales[i] != null && c.materiales[i].codigo.equals(codigo)) return c.materiales[i];
        }
        for (int i = 0; i <= c.posManoDeObra; i++) {
            if (c.manoDeObra[i] != null && c.manoDeObra[i].codigo.equals(codigo)) return c.manoDeObra[i];
        }
        for (int i = 0; i <= c.posMaquinariaYEquipo; i++) {
            if (c.maquinariaYEquipo[i] != null && c.maquinariaYEquipo[i].codigo.equals(codigo)) return c.maquinariaYEquipo[i];
        }
        for (int i = 0; i <= c.posServicios; i++) {
            if (c.servicios[i] != null && c.servicios[i].codigo.equals(codigo)) return c.servicios[i];
        }
        return null;
    }

    public static void bajasConceptos() {
        String codigo = dialogo("Introduce el codigo del Concepto").trim();
        Concepto nodo = buscarConcepto(codigo);
        if (nodo != null) {
            String opcion = dialogo("¿Deseas eliminar a " + nodo.insumo + "? [Y/N]").toUpperCase().trim();
            if (opcion.equals("Y")) {
                eliminarConcepto(codigo);
            }
        } else {
            System.out.println("El codigo no existe.");
        }
    }

    public static void eliminarConcepto(String codigo) {
        Catalogo c = catalogo;
        // Remover con corrimiento
        for (int i = 0; i <= c.posMateriales; i++) {
            if (c.materiales[i] != null && c.materiales[i].codigo.equals(codigo)) {
                for (int j = i; j < c.posMateriales; j++) {
                    c.materiales[j] = c.materiales[j + 1];
                }
                c.materiales[c.posMateriales] = null;
                c.posMateriales--;
                System.out.println("Concepto eliminado correctamente.");
                return;
            }
        }
        for (int i = 0; i <= c.posManoDeObra; i++) {
            if (c.manoDeObra[i] != null && c.manoDeObra[i].codigo.equals(codigo)) {
                for (int j = i; j < c.posManoDeObra; j++) {
                    c.manoDeObra[j] = c.manoDeObra[j + 1];
                }
                c.manoDeObra[c.posManoDeObra] = null;
                c.posManoDeObra--;
                System.out.println("Concepto eliminado correctamente.");
                return;
            }
        }
        for (int i = 0; i <= c.posMaquinariaYEquipo; i++) {
            if (c.maquinariaYEquipo[i] != null && c.maquinariaYEquipo[i].codigo.equals(codigo)) {
                for (int j = i; j < c.posMaquinariaYEquipo; j++) {
                    c.maquinariaYEquipo[j] = c.maquinariaYEquipo[j + 1];
                }
                c.maquinariaYEquipo[c.posMaquinariaYEquipo] = null;
                c.posMaquinariaYEquipo--;
                System.out.println("Concepto eliminado correctamente.");
                return;
            }
        }
        for (int i = 0; i <= c.posServicios; i++) {
            if (c.servicios[i] != null && c.servicios[i].codigo.equals(codigo)) {
                for (int j = i; j < c.posServicios; j++) {
                    c.servicios[j] = c.servicios[j + 1];
                }
                c.servicios[c.posServicios] = null;
                c.posServicios--;
                System.out.println("Concepto eliminado correctamente.");
                return;
            }
        }
    }

    public static void modificarConcepto() {
        String codigo = dialogo("Introduce el codigo del Concepto").trim();
        Concepto nodo = buscarConcepto(codigo);
        if (nodo != null) {
            System.out.println("Descripcion actual: " + nodo.descripcion);
            String opcion = dialogo("¿Deseas modificarla? [Y/N]").toUpperCase().trim();
            if (opcion.equals("Y")) {
                nodo.descripcion = dialogo("Nueva descripcion");
                System.out.println("Descripcion actualizada.");
            }
        } else {
            System.out.println("El codigo no existe.");
        }
    }

    public static void main(String[] args) {
        crearCatalogo();
        String[] menu = {
            "1.-Altas",
            "2.-Bajas",
            "3.-Modificar",
            "6.-Salida"
        };
        String opcion = "";
        while (!opcion.equals("6")) {
            opcion = desplegarMenu("SISTEMA DE CATALOGO", menu).trim();
        }
    }
}