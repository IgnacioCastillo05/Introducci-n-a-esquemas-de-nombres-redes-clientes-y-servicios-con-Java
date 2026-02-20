package rpc;

/**
 * Implementación real de la lógica del servidor.
 */
public class CalculatorServiceImpl implements CalculatorService {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int square(int n) {
        return n * n;
    }
}