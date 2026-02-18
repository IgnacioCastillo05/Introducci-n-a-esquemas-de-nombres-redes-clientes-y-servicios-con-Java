package rpc;

/**
 * Main de prueba del cliente RPC.
 * Llama los métodos como si fueran locales, el stub se encarga
 * de la comunicación con el servidor por debajo.
 */
public class RpcClientMain {

    public static void main(String[] args) {
        CalculatorService calc = new CalculatorClientStub("127.0.0.1", 5000);

        System.out.println("=== Prueba RPC ===");
        System.out.println("add(10, 3)  = " + calc.add(10, 3));
        System.out.println("add(6, 5) = " + calc.add(6, 5));
        System.out.println("square(8)  = " + calc.square(8));
        System.out.println("square(4)  = " + calc.square(4));
    }
}