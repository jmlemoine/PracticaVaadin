package com.example.demo.Design;

import com.example.demo.Model.Evento;
import com.example.demo.Model.Usuario;
import com.example.demo.Services.EventoService;
import com.example.demo.Services.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.calendar.CalendarComponent;
import org.vaadin.calendar.CalendarItemTheme;
import org.vaadin.calendar.data.AbstractCalendarDataProvider;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Route
@SpringComponent
@UIScope
public class Principal extends VerticalLayout {

    public static CalendarComponent<Evento> calendario = new CalendarComponent<Evento>()
            .withItemDateGenerator(Evento::getFecha)
            .withItemLabelGenerator(Evento::getTitulo)
            .withItemThemeGenerator(Evento::getColor);

    @Autowired
    public static EventoService eventoService;

    @Autowired
    public Principal(@Autowired final PantallaEvento pantallaEvento,
                     @Autowired UsuarioService usuarioService,
                     @Autowired EventoService eventoService,
                     @Autowired final PantallaEmail pantallaEmail,
                     @Autowired PantallaEventoModificar pantallaEventoModificar) {

        Principal.eventoService = eventoService;

        if (usuarioService.listarUsuarios().isEmpty()) {
            getUI().get().navigate("");
        }
        else if (!usuarioService.listarUsuarios().get(0).isEstaLogueado()) {

            getUI().get().navigate("");

        }
        else {

            setAlignItems(Alignment.CENTER);
            HorizontalLayout layoutBotones = new HorizontalLayout();
            layoutBotones.setSpacing(true);

            Button agregar = new Button("Agregar evento");
            Button enviarEmail = new Button("Enviar email");
            Button verUsuario = new Button("Información de usuario");
            Button CRUD = new Button("CRUD de Gerentes");
            Button salir = new Button("Salir");

            agregar.setIcon(new Icon(VaadinIcon.CALENDAR_CLOCK));
            agregar.getElement().setAttribute("theme", "primary");

            enviarEmail.setIcon(new Icon(VaadinIcon.CALENDAR_ENVELOPE));
            enviarEmail.getElement().setAttribute("theme", "primary");

            verUsuario.setIcon(new Icon(VaadinIcon.CLIPBOARD_USER));

            CRUD.setIcon(new Icon(VaadinIcon.FORM));
            CRUD.getElement().setAttribute("theme", "success");

            salir.setIcon(new Icon(VaadinIcon.SIGN_OUT));
            salir.getElement().setAttribute("theme", "error");

            configuraBotonPantalla(agregar, pantallaEvento);
            configuraBotonPantalla(enviarEmail, pantallaEmail);

            layoutBotones = new HorizontalLayout(agregar, enviarEmail, verUsuario, CRUD, salir);

            salir.addClickListener((evento) -> {

                try {
                    Usuario usuario = usuarioService.listarUsuarios().get(0);
                    usuario.setEstaLogueado(false);
                    usuarioService.editarUsuario(usuario);

                    getUI().get().navigate("");
                } catch (PersistenceException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            verUsuario.addClickListener((evento) -> getUI().get().navigate("usuario"));
            CRUD.addClickListener((evento) -> getUI().get().navigate("gerentes"));

            eventoService.crearEvento(
                    1,
                    Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    "Entrega Practica Vaadin",
                    CalendarItemTheme.Green
            );

            calendario.setDataProvider(new CustomDataProvider());
            calendario.addEventClickListener(evt -> {
                try {
                    pantallaEventoModificar.fecha.setValue(evt.getDetail().getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    pantallaEventoModificar.titulo.setValue(evt.getDetail().getTitulo());
                    abrirPantalla(pantallaEventoModificar);
                    eventoService.crearEvento(evt.getDetail().getId(), evt.getDetail().getFecha(), evt.getDetail().getTitulo(), evt.getDetail().getColor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            H4 titulo = new H4("Práctica #14 - OCJ");
            H6 subtitulo = new H6("Calendario");

            setAlignItems(Alignment.CENTER);

            add(titulo, subtitulo, layoutBotones, calendario);

        }

        Button agregar = new Button("Agregar");
        agregar.setIcon(new Icon(VaadinIcon.PLUS));
        agregar.getElement().setAttribute("theme", "primary");


    }

    private void abrirPantalla(VerticalLayout form) {

        Dialog vistaPantalla = new Dialog();
        vistaPantalla.add(form);
        vistaPantalla.open();

    }

    private void configuraBotonPantalla(Button boton, VerticalLayout formulario) {

        boton.addClickListener((e) -> {
            abrirPantalla(formulario);
        });

    }


}

@SpringComponent
@UIScope
class CustomDataProvider extends AbstractCalendarDataProvider<Evento> {

    @Override
    public Collection<Evento> getItems(Date fromDate, Date toDate) {

        List<Evento> eventos = Principal.eventoService.listarEventos();
        return eventos;

    }

}
