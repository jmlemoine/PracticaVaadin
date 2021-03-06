package com.example.demo.Design;

import com.example.demo.Model.Evento;
import com.example.demo.Services.EventoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.calendar.CalendarItemTheme;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@SpringComponent
@UIScope
public class PantallaEventoModificar extends VerticalLayout {

    DatePicker fecha = new DatePicker();
    TextField titulo = new TextField("Titulo");

    public PantallaEventoModificar(@Autowired EventoService eventoService) {

        FormLayout formLayout = new FormLayout();
        H3 header = new H3("Modificar Evento");

        fecha.setLabel("Selecciona el dia del evento");
        fecha.setPlaceholder("Selecciona una fecha");
        fecha.setValue(LocalDate.now());

        Button editar = new Button("Editar");
        editar.setIcon(new Icon(VaadinIcon.PENCIL));
        editar.getElement().setAttribute("theme", "success");

        Button cancelar = new Button("Cancelar");
        cancelar.setIcon(new Icon(VaadinIcon.CLOSE_CIRCLE_O));
        cancelar.getElement().setAttribute("theme", "success");

        HorizontalLayout botones = new HorizontalLayout(editar, cancelar);
        botones.setSpacing(true);

        formLayout.add(titulo, fecha);
        setAlignItems(Alignment.CENTER);

        add(header, formLayout, botones);

        editar.addClickListener((evento) -> {

            Evento e = new Evento(
                    (long) eventoService.listarEventos().size(),
                    Date.from(fecha.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    titulo.getValue(),
                    CalendarItemTheme.Blue
            );

            try {
                eventoService.crearEvento(
                        e.getId(),
                        e.getFecha(),
                        e.getTitulo(),
                        e.getColor()
                );
            }
            catch (Exception exp) {

                exp.printStackTrace();

            }
            Principal.calendario.refresh();

        });

    }

}
