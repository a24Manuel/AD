import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        int sel = 0;
        Scanner entrada = new Scanner(System.in);
        String ruta = ".";
        System.out.println("BIENVENIDO AL SISTEMA DE GESTIÓN DE ARCHIVOS!!!");
        System.out.println("Dirección a utilizar: " + ruta);
        while (sel != 9) {
            System.out.println("Seleccione que hacer:\n" +
                    "1 Listar archivos (en modo simple)\n" +
                    "2 Listar archivos (en modo detallado)\n" +
                    "3 Crear carpeta\n" +
                    "4 Copiar archivo\n" +
                    "5 Mover archivo\n" +
                    "6 Escribir archivo\n" +
                    "7 leer archivo\n" +
                    "8  Borrar instancia archivo\n" +
                    "9 Salir");

            sel = entrada.nextInt();
            entrada.nextLine();
            switch (sel) {
                case 1: //lectura simple
                    ListarCarpetaSimple(args, ruta);
                    break;


                case 2: //lectura detallada
                    ListarCarpetaDetallada(args, ruta);
                    break;

                case 3: // Crear carpeta
                    CrearCarpeta(ruta, entrada);

                    break;

                case 4: //Copiar archivo
                    CopiarArchivo(ruta, entrada);
                    break;

                case 5: //Mover archivo
                    MoverArchivo(ruta, entrada);
                    break;

                case 6: //Escribir archivo

                    try {
                        EscribirArchivo(ruta, entrada);
                    } catch (Exception e) {
                        System.out.println(e); // Imprimir en caso de error
                    }


                    break;

                case 7: //leer archivo


                    try {
                        LeerArchivo(ruta, entrada);
                    } catch (Exception e) {
                        System.out.println(e); // Imprimir en caso de error
                    }
                    break;

                case 8: //Eliminar elemento de archivo
                    BorrarInstanciaArchivo(ruta, entrada);
                    break;

                case 9: //salir
                    System.out.println("Gracias por usar nuestros servicios");
                    break;

                default:
                    System.out.println("OPCIÓN NO VALIDA, ESCOGE OTRA TARADO");
                    break;
            }


        }
    }


    public static void ListarCarpetaSimple(String[] args, String ruta) {

        if (args.length >= 1) ruta = args[0];
        File fich = new File(ruta);
        if (!fich.exists()) {
            System.out.println("El archivo no existe");
        } else {
            System.out.println(fich.getPath() + " Es un directorio. Contenidos:");
            File[] listado = fich.listFiles();
            String lectura, escritura, ejecucion;

            for (int i = 0; i <= listado.length - 1; i++) {
                if (listado[i].isFile()) {
                    System.out.println("(_) " + listado[i].getName());
                } else {
                    System.out.println("(/)" + listado[i].getName() + "");

                }
            }

        }
    }


    public static void ListarCarpetaDetallada(String[] args, String ruta) {
        if (args.length >= 1) ruta = args[0];
        File fich2 = new File(ruta);
        if (!fich2.exists()) {
            System.out.println("El archivo no existe");
        } else {
            System.out.println(fich2.getPath() + " Es un directorio. Contenidos:");
            File[] listado = fich2.listFiles();
            String lectura, escritura, ejecucion;
            for (int i = 0; i <= listado.length - 1; i++) {
                lectura = listado[i].canRead() ? "r" : "-";
                escritura = listado[i].canWrite() ? "w" : "-";
                ejecucion = listado[i].canExecute() ? "x" : "-";
                var instance = java.time.Instant.ofEpochMilli(listado[i].lastModified());
                var zonedDateTime = java.time.ZonedDateTime
                        .ofInstant(instance, java.time.ZoneOffset.of("+02:00"));
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String fechaFormateado = zonedDateTime.format(formato);

                if (listado[i].isFile()) {
                    System.out.print("(_) " + listado[i].getName());
                    listado[i].getFreeSpace();
                    long tamaño = listado[i].length();
                    System.out.println(" [ " + tamaño + " bytes," + lectura + escritura + ejecucion + ", "
                            + fechaFormateado + "]");
                } else {
                    System.out.println("(/)" + listado[i].getName() + " [" + lectura + escritura + ejecucion + ","
                            + fechaFormateado + "]");

                }
            }
        }


    }

    private static void CrearCarpeta(String ruta, Scanner entrada) {
        System.out.println("Introduce el nombre de la nueva carpeta");
        String nombre = entrada.nextLine();
        File directorio = new File(nombre);
        if (!directorio.exists()) {
            try {
                directorio.mkdirs();
                System.out.println("Carpeta creada");
                // Utilizando la clase File de Java.NIO: copiamos el contenido del arhivo original al nuevo archivo.
            } catch (Exception e) {
                System.out.println(e); // Imprimir en caso de error
            }
        }

    }

    private static void CopiarArchivo(String ruta, Scanner entrada) {
        File original;
        String nombre;
        do {
            System.out.println("Introduce el nombre completo de la ubicación del archivo (a nivel relativo al directorio actual)");
            nombre = entrada.nextLine(); //poner Origen/nombreArchivo
            original = new File(nombre);
            if (!original.exists()) {
                System.out.println("Error al encontrar archivo, introducir otro");
            }
        } while (!original.exists());

        File destino;
        do {
            System.out.println("Introduce el nombre completo de la ubicación de la carpeta a donde se copiara el archivo");
            nombre = entrada.nextLine(); //poner Destino
            destino = new File(nombre);
            if (!destino.exists()) {
                System.out.println("Error al encontrar archivo, introducir otro");
            }
        } while (!destino.exists());
        try {
            Files.copy(original.toPath(), Path.of(destino.toPath() + "/" + original.getName()));
            //IMPORTANTE: en este metodo se tiene que añadir al PATH el nombre del archivo a mover tipo Destino/nombreArchivo
            System.out.println("Archivo copiado");
            // Utilizando la clase File de Java.NIO: copiamos el contenido del arhivo original al nuevo archivo.
        } catch (Exception e) {
            System.out.println(e); // Imprimir en caso de error
        }
    }

    private static void MoverArchivo(String ruta, Scanner entrada) {
        File origen;
        String nombre;
        do {
            System.out.println("Introduce el nombre completo de la ubicación del archivo (a nivel relativo al directorio actual)");
            nombre = entrada.nextLine(); //poner ./Origen/nombreArchivo
            origen = new File(nombre);
            if (!origen.exists()) {
                System.out.println("Error al encontrar archivo, introducir otro");
            }
        } while (!origen.exists());
        File destino;
        do {
            System.out.println("Introduce el nombre completo de la ubicación  de la carpeta   a donde se movera el archivo");
            nombre = entrada.nextLine(); //poner Destino/nombreArchivo
            destino = new File(nombre);
            if (!destino.exists()) {
                System.out.println("Error al encontrar archivo, introducir otro");
            }
        } while (!destino.exists());

        try {
            Files.move(origen.toPath(), Path.of(destino.toPath() + "/" + origen.getName()));
            //IMPORTANTE: en este metodo se tiene que añadir al PATH el nombre del archivo a mover tipo Destino/nombreArchivo
            System.out.println("Archivo movido");
            // Utilizando la clase File de Java.NIO: movemos el archivo de origen a la carpeta de destino
        } catch (Exception e) {
            System.out.println(e); // Imprimir en caso de error
        }

    }

    private static void EscribirArchivo(String ruta, Scanner entrada) throws FileNotFoundException {
        /*
        File origen;
        String nombre;
        do {
            System.out.println("Introduce el nombre completo de la ubicación del archivo a leer");
            nombre = entrada.nextLine(); //poner ./Origen/nombreArchivo
            origen = new File(nombre);
            if (!origen.exists()) {
                System.out.println("Error al encontrar archivo, introducir otro");
            }
        } while (!origen.exists());
        try {
            FileReader leer = new FileReader(origen);
            BufferedReader lect = new BufferedReader(leer);
            String cadena;
            System.out.println("Escribe el elemento a añadir en el archivo:");
            String elemento = entrada.nextLine();
            int li = 0;
            try {
                while ((cadena = lect.readLine()) != null) {
                    //Problema: no se leia bien y se saltaba lineas
                    //Solución: No poner lect.readLine() dos veces, que hace que se pase otra linea al coso.
                    System.out.println(cadena);
                    if (cadena.contains("<"+elemento+">")) li++;
                }
                System.out.println("El elemento "+elemento+" aparece "+li+" veces");
            } catch (Exception e) {
                System.out.println(e);
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();

          //  Transformer().transform(origen, );
            XmlReader read = new XmlReader() {
                @Override
                public void readXML(WebRowSet caller, Reader reader) throws SQLException {
                }

                @Override
                public void readData(RowSetInternal caller) throws SQLException {


                    /*
                try {
                    FileReader leer = new FileReader(origen);
                    BufferedReader lect = new BufferedReader(leer);
                    String cadena;
                    int li = 0;
                    try {
                        while ((cadena = lect.readLine()) != null) {
                            //Problema: no se leia bien y se saltabaja lineas
                            //Solución: No poner lect.readLine() dos veces, que hace que se pase otra linea al coso.
                            System.out.println(cadena);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                }

            };

            FileWriter esc = new FileWriter(origen, true);
                    System.out.println("Escribe algo para el archivo para añadir:");
                    String añadido = entrada.nextLine();
                    esc.write(añadido);
                    esc.close();
        } catch (Exception e) {
            System.out.println( e); // Imprimir en caso de error
        }*/

    }

    private static void LeerArchivo(String ruta, Scanner entrada) throws FileNotFoundException {
        File origen;
        String nombre;
        do {
            System.out.println("Introduce el nombre completo de la ubicación del archivo a leer");
            nombre = entrada.nextLine(); //poner ./Origen/nombreArchivo
            origen = new File(nombre);
            if (!origen.exists()) {
                System.out.println("Error al encontrar archivo, introducir otro");
            }
        } while (!origen.exists());
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(origen);

            doc.getDocumentElement().normalize();
            System.out.println("Leyendo archivo... ");
            String nodoBase;
            NodeList listaPisos;
            do {
                System.out.println("Introduce el nodo a revisar del archivo");
                nodoBase = entrada.nextLine(); //poner Piso
                listaPisos = doc.getElementsByTagName(nodoBase);
                if (listaPisos.equals(null)) {
                    System.out.println("Error al encontrar nodo, introducir otro");
                }
            } while (listaPisos.equals(null));
            System.out.printf("<<LISTADO: " + doc.getDocumentElement().getNodeName() + " >>\n");
          //  System.out.println("ID    Dirección  Ciudad       Superficie      PrecioAlquiler");
            if (listaPisos.getLength() == 0) {
                System.out.println("ARCHIVO NO RECORRIDO");
                //PROBLEMA: listaPisos tiene longitud 0.
                //Solucion: recordar poner mayusculas
            }
            ;
            for (int i = 0; i < listaPisos.getLength(); i++) {
                Node nodo = listaPisos.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList nombres = nodo.getChildNodes();
                    if (i == 0) {
                        for (int x = 0; x <= nombres.getLength(); x++) {
                            Node element = nombres.item(x);
                            if (element != null && element.getNodeType() == Node.ELEMENT_NODE) {
                                System.out.printf("%-25s", element.getNodeName());
                            }
                            //Problema: me sale #TEXT y despues el nombre normal
                            //Solución: Creamos metodo IF para diferenciar el Type del propio elemento
                        }
                    }
                    System.out.println();
                    for (int x = 0; x < nombres.getLength(); x++) {
                        Node element = nombres.item(x);

                        if (element != null && element.getNodeType() == Node.ELEMENT_NODE) {
                            System.out.printf("%-25s", element.getTextContent());
                        }
                        //Error: java.lang.NullPointerException: Cannot invoke "org.w3c.dom.Node.getNodeName()" because "element" is null
                        //Solución: poner el if null en ambos apartados.
                    }
                }
            }
            System.out.println();
                /*

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;
                    System.out.println(elemento.getElementsByTagName(""));
                }
                    */



        } catch (ParserConfigurationException e) {
            System.out.println("1 " +e);
        } catch (IOException e) {
            System.out.println("2 " +e);
        } catch (SAXException e) {
            System.out.println("3 " +e);
        } catch (Exception e) {
            System.out.println("4 " +e); // Imprimir en caso de error
        }
         /*

                                origen.setReadable(true); //Nos aseguramos que el archivo se pueda leer
                                FileReader leer = new FileReader(origen);
                                BufferedReader lect = new BufferedReader(leer);
                                String cadena;
                                int li = 0;
                                    while ((cadena = lect.readLine()) != null) {
                                        //Problema: no se leia bien y se saltabaja lineas
                                        //Solución: No poner lect.readLine() dos veces, que hace que se pase otra linea al coso.
                                        System.out.println(cadena);
                                    }

                                //Creamos un objeto FileOutput para poder sacar en pantalla la información del File
                    */
    }

    private static void BorrarInstanciaArchivo(String ruta, Scanner entrada) {
        File origen;
        String nombre;
        do {
            System.out.println("Introduce el nombre completo de la ubicación del archivo a leer");
            nombre = entrada.nextLine(); //poner ./Origen/nombreArchivo
            origen = new File(nombre);
            if (!origen.exists()) {
                System.out.println("Error al encontrar archivo, introducir otro");
            }
        } while (!origen.exists());
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(origen);

            NodeList listaPisos;
            String nodoBase;
            do {
                System.out.println("Introduce el nodo a eliminar del archivo");
                nodoBase = entrada.nextLine(); //poner ./Origen/nombreArchivo
                listaPisos = doc.getElementsByTagName(nodoBase);
                if ( listaPisos == null || listaPisos.getLength() == 0) {
                    System.out.println("Error al encontrar nodo, introducir otro");
                }
            } while ( listaPisos == null || listaPisos.getLength() == 0);

          System.out.println("Hay "+listaPisos.getLength()+" elementos de "+ nodoBase+"\n Introduce el ID del que se va eliminar");
          int sel = entrada.nextInt();
           entrada.nextLine();
            if (sel < 1 || sel > listaPisos.getLength()) {
                System.out.println("ID NO ENCONTRADO, CANCELANDO PROCESO");
            } else {
                System.out.println("Confirmar eliminación de " + nodoBase + " con ID " + sel + "? (Introduce si para confirmar y no para cancelar)");
                boolean choice = false;
                    String escoge  = entrada.nextLine();
                    if (escoge.equals("si")) {
                        choice = true;
                    } else if (escoge.equals("no")) {

                    } else {
                        System.out.println("ENTRADA NO VALIDA");
                    }
                //Introducir
                //Problema: No importa que ponga en el escoge (si o no), me sale como entrada no valida
                //Solución: poner equals en el if.
                if (!choice) {
                    System.out.println("BORRADO CANCELADO");
                } else {

                    for (int i = 0; i < listaPisos.getLength(); i++) {
                        Node piso = listaPisos.item(i);
                        Element elemento = (Element) piso;
                        String id = elemento.getElementsByTagName("ID").item(0).getTextContent();
                        int idInt = Integer.parseInt(id);
                        if (idInt == sel) {  // Si es el ID del piso a eliminar
                            piso.getParentNode().removeChild(piso);
                            break;
                        }
                    }

                    // Guardar los cambios en el archivo
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(new File(origen.toURI()));
                    transformer.transform(source, result);

                    System.out.println("Piso eliminado.");
                }
            }

        } catch (Exception e) {
                e.printStackTrace();
        }

    }
}
/*
        private static String ListarArchivos() {
            String lista;

            return lista;
        }

        private static void CopiarArchivo () {

        }

        public class CrearCarpeta  (Scanner entrada) {

    }

        private static void MoverArchivo () {

        }




    }
    */