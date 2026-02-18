package rpc;

/**
 * Contrato del servicio - define qué operaciones están disponibles remotamente.
 */
public interface CalculatorService {
    int add(int a, int b);
    int square(int n);
}