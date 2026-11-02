package superlab.modelo;

public enum Incidente {
    VEHICULO_SOSPECHOSO(
            "Autocaravana estacionada durante mas de 3 horas y marcada como vigilada",
            "La autocaravana ha sido detectada y la operacion corre riesgo de ser descubierta"),
    FALTA_PROTECCION(
            "Stock de mascarillas agotado y operario sin proteccion",
            "El operario trabaja expuesto a los vapores y existe riesgo para su salud"),
    FALTA_VENTILACION(
            "Ventilador VEN01 apagado y concentracion de gas por encima del limite",
            "La casa clandestina acumula gas sin ventilacion y existe riesgo para quienes estan dentro."),
    OLOR_VECINOS(
            "Nivel de olor exterior elevado con el ventilador funcionando y alerta vecinal",
            "Los vecinos han detectado el olor y es posible que avisen a las autoridades"),
    EQUIPO_DESCALIBRADO(
            "Desviacion de calibracion superior al limite y equipo descalibrado",
            "El lote no cumple la pureza exigida y la produccion debe detenerse hasta recalibrar"),
    INDETERMINADO(
            "Los datos no coinciden con ningun incidente conocido",
            "No se puede concluir nada con la informacion disponible");

    private final String efectos;
    private final String conclusion;

    Incidente(String efectos, String conclusion){
        this.efectos = efectos;
        this.conclusion = conclusion;
    }

    public String getEfectos() {
        return efectos;
    }

    public String getConclusion() {
        return conclusion;
    }
}
