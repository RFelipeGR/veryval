package com.grupo8.openxava.model;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Required;

@Entity
public class RegistroAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Required
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Required
    private Time horaEntrada;

    private Time horaSalida;

    @Column(length = 40)
    private String estado;

    private boolean esRetraso;

    @ManyToOne
    @Required
    @DescriptionsList(descriptionProperties = "cedula, nombre")
    private Empleado empleado;

    @PrePersist
    @PreUpdate
    public void calcularEstadoAsistencia() {

        if (horaEntrada == null || empleado == null || empleado.getHorario() == null) {
            estado = "INCOMPLETO";
            esRetraso = false;
            return;
        }

        if (empleado.getHorario().getHoraEntrada() == null || empleado.getHorario().getHoraSalida() == null) {
            estado = "HORARIO INCOMPLETO";
            esRetraso = false;
            return;
        }

        LocalTime entradaRegistrada = horaEntrada.toLocalTime();
        LocalTime entradaEsperada = empleado.getHorario().getHoraEntrada().toLocalTime();
        LocalTime salidaEsperada = empleado.getHorario().getHoraSalida().toLocalTime();

        LocalTime limiteTolerancia = entradaEsperada.plusMinutes(15);

        esRetraso = entradaRegistrada.isAfter(limiteTolerancia);

        if (horaSalida == null) {
            estado = esRetraso ? "EN CURSO CON RETRASO" : "EN CURSO";
            return;
        }

        LocalTime salidaRegistrada = horaSalida.toLocalTime();

        if (!salidaRegistrada.isAfter(entradaRegistrada)) {
            estado = "INCONSISTENTE";
            esRetraso = false;
        } else if (salidaRegistrada.isBefore(salidaEsperada)) {
            estado = esRetraso ? "SALIDA ANTICIPADA CON RETRASO" : "SALIDA ANTICIPADA";
        } else if (esRetraso) {
            estado = "COMPLETO CON RETRASO";
        } else {
            estado = "COMPLETO";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(Time horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Time getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(Time horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isEsRetraso() {
        return esRetraso;
    }

    public void setEsRetraso(boolean esRetraso) {
        this.esRetraso = esRetraso;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
}