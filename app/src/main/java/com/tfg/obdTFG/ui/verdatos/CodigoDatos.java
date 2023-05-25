package com.tfg.obdTFG.ui.verdatos;

public enum CodigoDatos {
    VelocidadVehiculo("010D", "Velocidad del vehículo"),
    RPM("010C", "Revoluciones por minuto"),
    VelocidadFlujoAire("1010", "Velocidad del flujo del aire MAF"),
    CargaCalculadaMotor("0104", "Carga calculada del motor"),
    TempAceiteMotor("015C", "Temperatura del aceite del motor"),
    PosicionAcelerador("0111", "Posición del acelerador"),
    PorcentajeTorqueSolicitado("0161", "Porcentaje torque solicitado"),
    PorcentajeTorqueActual("0162", "Porcentaje torque actual"),
    TorqueReferenciaMotor("0163", "Torque referencia motor"),
    VoltajeModuloControl("0142", "Voltaje módulo control"),
    PresionBarometricaAbsoluta("0133", "Presión barométrica absoluta"),
    PresionCombustible("010A", "Presión del combustible"),
    PresionMedidorTrenCombustible("0123", "Presión medidor tren combustible"),
    PresionAbsColectorAdmision("010B", "Presion absoluta colector admisión"),
    PresionVaporSisEvaporativo("0132", "Presión del vapor del sistema evaporativo"),
    NivelCombustible("012F", "Nivel de combustible %"),
    TipoCombustibleNombre("0151", "Tipo de combustible"),
    VelocidadConsumoCombustible("015E", "Velocidad consumo de combustible"),
    RelacionCombustibleAire("0144", "Relación combustible-aire"),
    DistanciaLuzEncendidaFalla("0121", "Distancia con luz fallas encendida"),
    EGRComandado("012C", "EGR comandado"),
    FallaEGR("012D", "Falla EGR"),
    PurgaEvaporativaComand("012E", "Purga evaporativa comandada"),
    CantidadCalentamientosDesdeNoFallas("0130", "Cant. calentamiento sin fallas"),
    DistanciaRecorridadSinLuzFallas("0131", "Distancia sin luz fallas encendida"),
    SincroInyeccionCombustible("015D", "Sincronización inyección combustible"),
    TempAireAmbiente("0146", "Temperatura del aire ambiente"),
    TempLiquidoEnfriamiento("0105", "Tº del líquido de enfriamiento"),
    TempAireColectorAdmision("010F", "Tº del aire del colector de admisión"),
    TempCatalizador("013C", "Temperatura del catalizador"),
    TiempoMotorEncendido("010D", "Tiempo con el motor encendido"),
    VelocidadMedia("011F", "Velocidad media del viaje"),
    ConsumoMedio("015E", "Consumo medio del viaje");

    private final String codigo;
    private final String nombre;

    private CodigoDatos(final String codigo, final String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    //devuelve el valor
    public String getCodigo() {
        return codigo;
    }

    //devuelve el nombre del tipo de combustible
    public String getNombre() {
        return nombre;
    }
}
