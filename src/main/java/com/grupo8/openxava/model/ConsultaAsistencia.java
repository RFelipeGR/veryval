package com.grupo8.openxava.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.View;
import org.openxava.calculators.CurrentDateCalculator;
import org.openxava.jpa.XPersistence;

@Entity
@View(members =
        "CriteriosConsulta {" +
                "fechaInicio, fechaFin;" +
                "empleado;" +
                "}" +
                "ResultadosAsistencia {" +
                "registros;" +
                "}"
)
public class ConsultaAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Temporal(TemporalType.DATE)
    @DefaultValueCalculator(CurrentDateCalculator.class)
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    @DefaultValueCalculator(CurrentDateCalculator.class)
    private Date fechaFin;

    @ManyToOne
    @DescriptionsList(descriptionProperties = "cedula, nombre")
    private Empleado empleado;

    @Transient
    private List<RegistroAsistencia> registros;

    @ReadOnly
    @ListProperties("fecha, empleado.cedula, empleado.nombre, horaEntrada, horaSalida, estado, esRetraso")
    public List<RegistroAsistencia> getRegistros() {

        if (fechaInicio == null || fechaFin == null) {
            return new ArrayList<>();
        }

        String jpql =
                "from RegistroAsistencia r " +
                        "where r.fecha between :fechaInicio and :fechaFin ";

        if (empleado != null) {
            jpql += "and r.empleado = :empleado ";
        }

        jpql += "order by r.fecha desc, r.empleado.nombre asc";

        var query = XPersistence.getManager()
                .createQuery(jpql, RegistroAsistencia.class)
                .setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin);

        if (empleado != null) {
            query.setParameter("empleado", empleado);
        }

        registros = query.getResultList();
        return registros;
    }

    public void setRegistros(List<RegistroAsistencia> registros) {
        this.registros = registros;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
}