package com.parroquiasanleandro.fecha;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class Fecha {
	public final static String CALENDARIO = "Calendario";

	//ATRIBUTOS
	public int dia;
	public Mes mes;
	public int año;
	public int hora;
	public int minuto;
	public int segundo;
	public DiasSemana diaSemana;

	//CONSTRUCTORES
	//Constructor vacio
	public Fecha() {
	}

	/**
	 * Constructor para inicializar con dia, mes y anno
	 *
	 * @param dia
	 * @param mes
	 * @param año
	 */
	public Fecha(int dia, Mes mes, int año) {
		this.dia = dia;
		this.mes = mes;
		this.año = año;
		actualizarDiaSemana();
	}

	/**
	 * Constructor para inicializar con hora, minuto y segundo
	 *
	 * @param hora
	 * @param minuto
	 * @param segundo
	 */
	public Fecha(int hora, int minuto, int segundo) {
		this.hora = hora;
		this.minuto = minuto;
		this.segundo = segundo;
	}

	/**
	 * Constructor para inicializar con hora y minuto
	 *
	 * @param hora
	 * @param minuto
	 */
	public Fecha(int hora, int minuto) {
		this.hora = hora;
		this.minuto = minuto;
		this.segundo = 0;
	}

	/**
	 * Constructor para inicializar con todos los atributos
	 *
	 * @param dia
	 * @param mes
	 * @param año
	 * @param hora
	 * @param minuto
	 * @param segundo
	 */
	public Fecha(int dia, Mes mes, int año, int hora, int minuto, int segundo) {
		this.dia = dia;
		this.mes = mes;
		this.año = año;
		this.hora = hora;
		this.minuto = minuto;
		this.segundo = segundo;
		actualizarDiaSemana();
	}

	//METODOS

	/**
	 * Metodo para que la fecha pase a ser la fecha actual del sistema
	 */
	public void convertirAFechaActual() {
		Calendar hoy = Calendar.getInstance();
		this.dia = hoy.get(Calendar.DAY_OF_MONTH);
		this.mes = Mes.values()[hoy.get(Calendar.MONTH)];
		this.año = hoy.get(Calendar.YEAR);
		this.diaSemana = DiasSemana.values()[hoy.get(Calendar.DAY_OF_WEEK) - 2];
	}

	/**
	 * Metodo que devuelve un objeto Fecha con la fecha actual del sistema
	 *
	 * @return fecha
	 */
	public static Fecha FechaActual() {
		Calendar hoy = Calendar.getInstance();
		return calendarToFecha(hoy);
	}

	/**
	 * Metodo que devuelve los millisegungos actuales
	 */
	public static long getActualMillis() {
		Calendar hoy = Calendar.getInstance();
		return hoy.getTimeInMillis();
	}

	/**
	 * Metodo para actualizar el día de la semana de la fecha actual
	 * Para ello crea un objeto calendar al que se le pasan el año, el mes y el día y con esto se calcula
	 * y se guarda en la variable diaSeman
	 */
	public void actualizarDiaSemana() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(año, mes.getNumeroMes() - 1, dia);
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			this.diaSemana = DiasSemana.Domingo;
		} else {
			this.diaSemana = DiasSemana.values()[calendar.get(Calendar.DAY_OF_WEEK) - 2];
		}
	}

	/**
	 * Metodo que asigna el valor 1 al día convirtiendolo en el primer día de ese mes
	 * Después actualiza la el dia de la semana
	 */
	public void convertirAPrimerDiaMes() {
		this.dia = 1;
		actualizarDiaSemana();
	}

	public void sumMinutos(int minutos) {
		int sumaMinutos = minuto + minutos;
		if (sumaMinutos > 60) {
			sumHoras(sumaMinutos / 60);
			this.minuto = sumaMinutos % 60;
		} else {
			this.minuto = sumaMinutos;
		}
	}

	public void sumHoras(int horas) {
		int sumaHoras = hora + horas;
		if (sumaHoras > 24) {
			sumDias(sumaHoras / 24);
			this.hora = sumaHoras % 24;
		} else {
			this.hora = sumaHoras;
		}
	}

	public void sumDias(int dias) {
		int sumaDias = dia + dias;
		if (sumaDias > mes.getNumDiasMes()) {
			sumMeses(sumaDias / mes.getNumDiasMes());
			this.dia = (sumaDias % mes.getNumDiasMes()) + 1;
		} else {
			this.dia = sumaDias;
		}
	}

	public void sumMeses(int meses) {
		int absMeses = Math.abs(meses);
		int sumaMeses = mes.getNumeroMes() + meses;

		if (meses > 0 || sumaMeses > 0) {
			if (sumaMeses > 12) {
				sumAños(sumaMeses / 12);
				this.mes = Mes.values()[(sumaMeses % 12) - 1];
			} else {
				this.mes = Mes.values()[sumaMeses - 1];
			}
		} else {
			if (absMeses > 12) {
				sumAños(-absMeses / 12);
				sumMeses(-absMeses % 12);
			} else {
				sumAños(-1);
				this.mes = Mes.values()[(12 + sumaMeses) - 1];
			}
		}
	}

	public void sumAños(int año) {
		this.año += año;
	}


	/**
	 * Metodo booleano que determina si un anno es o no bisiesto
	 * <p>Un anno es bisiesto si:
	 * Es divisible entre 4 y no lo es de 100
	 * Es divisible entre 4, entre 100 y entre 400
	 * En cualquier otro caso el anno no es bisiesto</p>
	 *
	 * @return {@code true} Si el anno es bisiesto
	 * {@code false} Si el anno no es bisiesto
	 */
	public boolean bisiesto() {
		if (año % 4 == 0) {
			if (año % 100 == 0) {
				return año % 400 == 0;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * Metodo booleano estatico que determina si un anno pasado por parametro es o no bisiesto
	 * <p>La lógica del metodo es
	 * la misma que la del metodo bisiesto no estatico que no recibe ningún parametro
	 * Un anno es bisiesto si:
	 * Es divisible entre 4 y no lo es de 100
	 * Es divisible entre 4, entre 100 y entre 400
	 * En cualquier otro caso el anno no es bisiesto</p>
	 *
	 * @param anno int que representa un anno
	 * @return {@code true} Si el anno es bisiesto
	 * {@code false} Si el anno no es bisiesto
	 */
	public static boolean bisiesto(int anno) {
		if (anno % 4 == 0) {
			if (anno % 100 == 0) {
				return anno % 400 == 0;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * Metodo estatico booleano que comprueba si es posible que exista un numero de dia
	 * del mes pasado por parametro
	 * El dia existe si:
	 * Es mayor a 0 y es menor que 31
	 *
	 * @param dia int que equivale al dia que se va a comprobar si existe o no
	 * @return {@code true} Si el día existe
	 * {@code false} Si el día no existe
	 */
	public static boolean existeDia(int dia) {
		return dia > 0 && dia < 32;
	}

	/**
	 * Metodo estatico y booleano que compruba si es posible que exista un mes cuyo
	 * numero se pasa por parametro
	 * El mes existe si:
	 * Es mayor a 0 a y menor a 13
	 *
	 * @param mes int que equivale al mes que se va a comprobar si existe o no
	 * @return {@code true} Si el mes existe
	 * {@code false} Si el mes no existe
	 */
	public static boolean existeMes(int mes) {
		return mes > 0 && mes <= 12;
	}

	/**
	 * Metodo booleano para comprobar si el dia es posible en el mes
	 * Se da por sentado que los valores del dia y el mes son posibles
	 * Se pasa el numero que la variable mes de la clase Mes tiene asignada
	 * para así poder trabajar con el numero del mes y no con el nombre del mismo
	 * El dia es posible si:
	 * Si el mes es 4,6,9 u 11 y el dia es menor que 31
	 * Siendo lo anterior y el mes distinto a 2
	 * Siendo el mes 2 y el dia menor que 30
	 *
	 * @param dia int que equivale al mes que se va a comprobar si existe o no
	 * @param mes int que equivale al mes que se va a comprobar si existe o no
	 * @return {@code true} Si el dia del mes es posible
	 * {@code false} Si el dia del mes no es posible
	 */
	public static boolean mes_diaValido(int dia, int mes) {
		if (dia == 31 && (mes == 4 || mes == 6 || mes == 9 || mes == 11)) {
			return false;
		} else {
			return !(dia >= 30 && mes == 2);
		}
	}

	/**
	 * Metodo estatico y booleano que comprueba si el anno pasado por parametro existe
	 * Se considera que el anno existe si:
	 * Si es mayor a 1900 y menor que 3000
	 * Esto se debe a que annos anteriores y posterios a dichas fechas respectivamente no son
	 * utiles
	 *
	 * @param año int que equivale al anno que se va a comprobar si existe o no
	 * @return {@code true} Si el anno existe
	 * {@code false} Si el anno no existe
	 */
	public static boolean existeaño(int año) {
		return año > 1900 || año < 3000;
	}

	/**
	 * Metodo booleano que comprueba si la fecha es posible y existe
	 * Se comprueba si existe el dia, si existe el mes y si existe el anno
	 * tambien se comprueba si el dia del mes es valido.
	 * En caso de lo anterior sea cierto comprueba que
	 * si el dia es 29 de Febrero(mes 2) el anno debe ser bisiesto
	 *
	 * @return {@code true} Si la fecha existe y es correcta
	 * {@code false} Si no existe y/o no es correcta
	 */
	public boolean fechaValida() {
		if (Fecha.existeDia(dia)
				&& Fecha.existeMes(mes.getNumeroMes())
				&& Fecha.existeaño(año)
				&& Fecha.mes_diaValido(dia, mes.getNumeroMes())) {
			return !(dia == 29 && mes.getNumeroMes() == 2 && !bisiesto());
		} else {
			return false;
		}
	}

	/**
	 * Metodo estatico y booleano que comprueba si la hora pasada por parametro existe
	 * La hora existe si:
	 * Es mayor o igual a 0 y menor de 60
	 *
	 * @param hora int que equivale a la hora que se va a comprobar si existe o no
	 * @return {@code true} Si la hora existe
	 * {@code false} Si la hora no existe
	 */
	public static boolean existeHora(int hora) {
		return (hora >= 0 || hora < 24);
	}

	/**
	 * Metodo estatico y booleano que comprueba si el minuto pasado por parametro existe
	 * El minuto existe si:
	 * Es mayor o igual a 0 y menor de 60
	 *
	 * @param minuto int que equivale a la hora que se va a comprobar si existe o no
	 * @return {@code true} Si el minuto existe
	 * {@code false} Si el minuto no existe
	 */
	public static boolean existeMinuto(int minuto) {
		return (minuto >= 0 || minuto < 60);
	}

	/**
	 * Metodo estatico y booleano que comprueba si el segundo pasado por parametro existe
	 * El segundo existe si:
	 * Es mayor o igual a 0 y menor de 60
	 *
	 * @param segundo int que equivale a la hora que se va a comprobar si existe o no
	 * @return {@code true} Si el segundo existe
	 * {@code false} Si el segundo no existe
	 */
	public static boolean existeSegundo(int segundo) {
		return (segundo >= 0 || segundo < 60);
	}

	/**
	 * método estático  que devuelve un entero que es la diferencia de dias entre dos fechas
	 * El método no hace caso de las horas,minutos y segundos
	 * Variables locales:
	 * variables int día1,día2,mes1,mes2,anno1,anno2 que se igualaran y servirán
	 * como variables de las dos fechas pasados por parámetros
	 * Array int diaMeses que indica el numero de días que tiene cada uno de los meses
	 * (Enero esta en la posición 1 del array)
	 * variable int diferencia que servirá para ir acumulando la diferencia de días entre las dos fechas
	 * <p>
	 * Antes de iniciar el algoritmo para comprobar la diferencia de fechas, comproprobamos, mediante el metodo
	 * isFecha1MayorQueFecha2, si la fecha1 es mayor que la fecha2 y en ese caso intercambiaremos las variables
	 * de tal forma que dia1, mes1 y año1 corresponderan a fecha2 y dia2, mes2 y año2 corresponderan a fecha1,
	 * de esta forma no aseguraremos de que calculamos correctamente los días de diferencia.
	 * Al terminar el algorirmo, si la fecha1 es mayor a la fecha2, devolveremos la diferencia de días entre las
	 * dos fechas como un número negativo
	 * <p>
	 * fecha1 < fecha2 = diferencia
	 * fecha1 > fecha2 = -diferencia
	 * </p>
	 * <p>
	 * Algoritmo del método:
	 * Primero comprobamos si las dos fechas están en el mismo anno. En caso de que las fechas
	 * estén en el mismo anno comprobamos si está en el mismo mes. Si la fechas están en el mismo mes
	 * y anno comprobamos si el día es el mismo en cuyo caso la diferencia seria igual a 0
	 * en caso contrario restamos los días
	 * Si el anno es el mismo pero el mes distinto sumamos los días de los meses que hay
	 * entre las dos fechas. Si el mes es febrero(2) y el anno es bisiesto sumamos uno a la diferencia.
	 * Si el mes de la fecha1 (mes1) es febrero sumamos 1. Después sumamos los días que quedan
	 * para completa el mes1 y los días (día2) que llevan transcurridos del mes de la fecha2 (mes2)
	 * </p>
	 * En caso de que los annos sean diferentes, con un bucle for sumamos 365 días a la diferencia
	 * por cada anno de diferencia entre el anno1 y el anno2 y vamos comprobando si alguno de esos
	 * annos es bisiesto en cuyo caso sumamos un día más por cada anno bisiesto. A continuación
	 * sumamos los días de los meses que quedan para completar el anno1 y si el anno es
	 * bisiesto y uno de los meses a sumar es febrero(2) sumamos 1. Sumamos los días del mes1
	 * y si el mes es febrero(2) y el anno bisiesto sumamos 1. Sumamos los días de los meses
	 * del anno2 hasta el mes2. Si el anno2 es bisiesto y uno de los meses de los que se suman
	 * los días es febrero se suma 1. Por último se suman los días del mes2 (día2):
	 *
	 * @param fecha1 parámetro tipo fecha que hace referencia a la primera fecha de entre las cuales
	 *               se quiere conocer la diferencia de fecha.
	 * @param fecha2 parámetro tipo fecha que hace referencia a la segunda fecha de entre las cuales
	 *               se quiere conocer la diferencia de fecha. Se recomienda que la segunda fecha sea
	 *               posterior que la primera fecha, en caso de que esto no ocurra se retornará un valor negativo
	 * @return diferencia
	 * variable local de tipo int que irá acumulando a lo largo del método la diferencia
	 * días que hay entre las dos fechas, en caso de que fecha1>fecha2 tendrá valor negativo
	 */
	public static int difereciaFecha(Fecha fecha1, Fecha fecha2) {
		int dia1, dia2, mes1, mes2, año1, año2;
		int[] diasMeses = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		int diferencia = 0;

		if (isFecha1MayorQueFecha2(fecha1, fecha2)) {
			dia1 = fecha2.dia;
			mes1 = fecha2.mes.getNumeroMes();
			año1 = fecha2.año;
			dia2 = fecha1.dia;
			mes2 = fecha1.mes.getNumeroMes();
			año2 = fecha1.año;
		} else {
			dia1 = fecha1.dia;
			mes1 = fecha1.mes.getNumeroMes();
			año1 = fecha1.año;
			dia2 = fecha2.dia;
			mes2 = fecha2.mes.getNumeroMes();
			año2 = fecha2.año;
		}
		if (año1 == año2) {
			if (mes1 == mes2) {
				if (dia1 == dia2) {
					return 0;
				} else {
					diferencia = dia2 - dia1;
				}
			} else {
				for (int i = (mes2 - 1); i > mes1; i--) {
					diferencia += diasMeses[i];
					if (i == 2) {
						if (bisiesto(año2)) {
							diferencia++;
						}
					}
				}
				if (mes1 == 2) {
					if (bisiesto(año2)) {
						diferencia++;
					}
				}
				diferencia += (diasMeses[mes1] - dia1) + dia2;
			}
		} else {
			for (int i = (año2 - 1); i > (año1 + 1); i--) {
				diferencia += 365;
				if (bisiesto(i)) {
					diferencia++;
				}
			}
			for (int i = 12; i > mes1; i--) {
				diferencia += diasMeses[i];
				if (i == 2) {
					if (bisiesto(año1)) {
						diferencia++;
					}
				}
			}
			diferencia += (diasMeses[mes1] - dia1);
			if (mes1 == 2) {
				if (bisiesto(año1)) {
					diferencia++;
				}
			}
			for (int i = (mes2 - 1); i >= 1; i--) {
				diferencia += diasMeses[i];
				if (i == 2) {
					if (bisiesto(año2)) {
						diferencia++;
					}
				}
			}
			diferencia += dia2;
		}
		if (isFecha1MayorQueFecha2(fecha1, fecha2)) {
			return -diferencia;
		} else {
			return diferencia;
		}
	}

	/**
	 * Metodo booleano para saber si la diferencia entre dos fechas introducidas es mayor que el numero de annos
	 * introducidos
	 *
	 * @param fecha1
	 * @param fecha2
	 * @param numAnnos
	 * @return
	 */
	public static boolean diferenciaFechaMayorQueAnnos(Fecha fecha1, Fecha fecha2, int numAnnos) {
		if ((fecha2.año - fecha1.año) > numAnnos) {
			return true;
		} else {
			if ((fecha2.año - fecha1.año) == numAnnos) {
				if (fecha2.mes.getNumeroMes() > fecha1.mes.getNumeroMes()) {
					return false;
				} else {
					if (fecha2.mes.getNumeroMes() == fecha1.mes.getNumeroMes()) {
						return fecha2.dia >= fecha1.dia;
					} else {
						return true;
					}
				}
			} else {
				return false;
			}
		}
	}

	public static boolean isFecha1MayorQueFecha2(Fecha fecha1, Fecha fecha2) {
		if (fecha1.año > fecha2.año) {
			return true;
		} else if (fecha1.año == fecha2.año) {
			if (fecha1.mes.getNumeroMes() > fecha2.mes.getNumeroMes()) {
				return true;
			} else if (fecha1.mes.getNumeroMes() == fecha2.mes.getNumeroMes()) {
				return fecha1.dia > fecha2.dia;
			}
		}
		return false;
	}

	public boolean isHoraEntreDosHoras(Fecha fecha1, Fecha fecha2) {
		if (this.hora < fecha1.hora) {
			return false;
		} else if (this.hora == fecha1.hora) {
			if (fecha1.hora == fecha2.hora) {
				return fecha1.minuto < this.minuto && this.minuto < fecha2.minuto;
			} else return this.minuto >= fecha1.minuto;
		} else {
			if (this.hora < fecha2.hora) {
				return true;
			} else if (this.hora == fecha2.hora) {
				return this.minuto <= fecha2.minuto;
			} else {
				return false;
			}
		}
	}

	public boolean esIgualA(Fecha fecha) {
		return fecha.dia == dia && fecha.mes == mes && fecha.año == año && fecha.hora == hora && fecha.minuto == minuto;
	}

	public static String formatearNumero(int numero) {
		if (String.valueOf(numero).length() != 2) {
			return "0" + numero;
		} else {
			return String.valueOf(numero);
		}
	}

	public static class FormatosFecha {
		public static final String HH_mm = "HH:mm";
		public static final String dd_MM_aaaa = "dd-MM-aaaa";
		public static final String dd_MM_aaaa_HH_MM = "dd-MM-aaaa HH:mm";
		public static final String EE_d_MMM_aaaa = "EE, d MMM aaaa";
		public static final String aaaaMM = "aaaaMM";
		public static final String MMMM_aaaa = "MMMM_aaaa";
	}

	public static Fecha toFecha(long millis){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendarToFecha(calendar);
	}

	public static Fecha calendarToFecha(Calendar calendar){
		return new Fecha(calendar.get(Calendar.DAY_OF_MONTH),
				Mes.values()[calendar.get(Calendar.MONTH)],
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND));
	}

	@NotNull
	@Override
	public String toString() {
		return (toString(""));
	}

	public String toString(String formato) {
		switch (formato) {
			case FormatosFecha.HH_mm:
				return formatearNumero(hora) + ":" + formatearNumero(minuto);
			case FormatosFecha.dd_MM_aaaa:
				return formatearNumero(dia) + "-" + formatearNumero(mes.getNumeroMes()) + "-" + año;
			case FormatosFecha.dd_MM_aaaa_HH_MM:
				return formatearNumero(dia) + "-" + formatearNumero(mes.getNumeroMes()) + "-" + año + " " + formatearNumero(hora) + ":" + formatearNumero(minuto);
			case FormatosFecha.EE_d_MMM_aaaa:
				return diaSemana.getAbrebiatura() + ", " + dia + " " + mes.getAbrebiatura() + " " + año;
			case FormatosFecha.aaaaMM:
				return año + Fecha.formatearNumero(mes.getNumeroMes());
			case FormatosFecha.MMMM_aaaa:
				return mes.name() + " " + año;
			default:
				return diaSemana.toString() + ", " + dia + " de " + mes.toString() + " de " + año;
		}
	}
}