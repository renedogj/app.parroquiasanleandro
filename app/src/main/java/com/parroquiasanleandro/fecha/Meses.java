package com.parroquiasanleandro.fecha;

/**
 * Clase enum para la utilización de los meses con nombres y no con numeros
 * Cada mes tiene asignado un numero (Según el mes del año que sean)
 */
public enum Meses {
    Enero(1),
    Febrero(2),
    Marzo(3),
    Abril(4),
    Mayo(5),
    Junio(6),
    Julio(7),
    Agosto(8),
    Septiembre(9),
    Octubre(10),
    Noviembre(11),
    Diciembre(12);

    private final int numeroMes;
    private final String[] abrebiaturas = {"",
            "en.",
            "febr,",
            "mzo.",
            "abr.",
            "my.",
            "jun.;",
            "jul.",
            "ago.",
            "sept.",
            "oct.",
            "nov.",
            "dic."
    };
    private final int[] diasMeses = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    Meses(int numeroMes) {
        this.numeroMes = numeroMes;
    }

    public int getNumeroMes() {
        return numeroMes;
    }

    public String getAbrebiatura() {
        return abrebiaturas[numeroMes];
    }

    public int getNumDiasMes(){
        return diasMeses[numeroMes];
    }
}
