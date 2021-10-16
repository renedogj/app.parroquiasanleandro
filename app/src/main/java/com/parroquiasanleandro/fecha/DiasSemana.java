package com.parroquiasanleandro.fecha;

/**
 * Clase enum para la utilización de los días de la semana con nombres y no con numeros
 * Cada día tiene asignado un numero (Según el día de la semana que sean)
 */
public enum DiasSemana {
    Lunes(1),
    Martes(2),
    Miercoles(3),
    Jueves(4),
    Viernes(5),
    Sabado(6),
    Domingo(7);

    private final int numeroDia;
    private final String[] abrebiaturas = {"",
            "Lun.",
            "Mar.",
            "Mié.",
            "Jue.",
            "Vie.",
            "Sáb.",
            "Dom."
    };

    DiasSemana(int numeroDia){
        this.numeroDia = numeroDia;
    }

    public int getNumeroDia(){
        return numeroDia;
    }

    public String getAbrebiatura(){
        return abrebiaturas[numeroDia];
    }
}
