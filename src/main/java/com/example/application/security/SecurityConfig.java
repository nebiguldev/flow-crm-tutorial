package com.example.application.security;

import com.example.application.data.Company;
import com.example.application.data.Contact;
import com.example.application.data.Status;
import com.example.application.views.LoginView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Autowired
    private DataSource dataSource;

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager();
        userDetailsManager.setDataSource(dataSource);
        userDetailsManager.setUsersByUsernameQuery("SELECT email, password, 'true' as enabled FROM contact WHERE email = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery("SELECT email, role FROM contact WHERE email = ?");
        return userDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(
                        AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/images/*.png")).permitAll());
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    public class ContactForm extends FormLayout {
        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");
        EmailField email = new EmailField("Email");
        PasswordField password = new PasswordField("Password");
        ComboBox<Status> status = new ComboBox<>("Status");
        ComboBox<Company> company = new ComboBox<>("Company");
        TextField role = new TextField("Role");
        Button save = new Button("Save");
        Button delete = new Button("Delete");
        Button close = new Button("Cancel");

        Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

        @Autowired
        private PasswordEncoder passwordEncoder;

        public ContactForm(List<Company> companies, List<Status> statuses) {
            addClassName("contact-form");
            binder.bindInstanceFields(this);

            company.setItems(companies);
            company.setItemLabelGenerator(Company::getName);
            status.setItems(statuses);
            status.setItemLabelGenerator(Status::getName);

            add(firstName,
                    lastName,
                    email,
                    password,
                    company,
                    status,
                    role,
                    createButtonsLayout());
        }

        private Component createButtonsLayout() {
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            save.addClickShortcut(Key.ENTER);
            close.addClickShortcut(Key.ESCAPE);

            save.addClickListener(event -> validateAndSave());
            delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
            close.addClickListener(event -> fireEvent(new CloseEvent(this)));

            binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
            return new HorizontalLayout(save, delete, close);
        }

        private void validateAndSave() {
            if (binder.isValid()) {
                Contact contact = binder.getBean();
                // Parolayı şifrele
                String encodedPassword = passwordEncoder.encode(contact.getPassword());
                contact.setPassword(encodedPassword);
                fireEvent(new SaveEvent(this, contact));
            }
        }

        public void setContact(Contact contact) {
            binder.setBean(contact);
        }

        // Events
        public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
            private Contact contact;

            protected ContactFormEvent(ContactForm source, Contact contact) {
                super(source, false);
                this.contact = contact;
            }

            public Contact getContact() {
                return contact;
            }
        }

        public static class SaveEvent extends ContactFormEvent {
            SaveEvent(ContactForm source, Contact contact) {
                super(source, contact);
            }
        }

        public static class DeleteEvent extends ContactFormEvent {
            DeleteEvent(ContactForm source, Contact contact) {
                super(source, contact);
            }
        }

        public static class CloseEvent extends ContactFormEvent {
            CloseEvent(ContactForm source) {
                super(source, null);
            }
        }

        public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
            return addListener(DeleteEvent.class, listener);
        }

        public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
            return addListener(SaveEvent.class, listener);
        }

        public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
            return addListener(CloseEvent.class, listener);
        }
    }
}
