package ru.umerenkov.adm.javatest2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@SpringUI
@Theme("mytheme")
public class Javatest2UI extends UI {

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		Dao MyDao = (Dao) MyListener.getCurrentWebApplicationContext().getBean("dao");

		final VerticalLayout layout = new VerticalLayout();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru"));
		Locale myLocale = new Locale("ru");

		DateField startDate = new DateField();
		startDate.setRequired(true);
		startDate.setResolution(Resolution.SECOND);
		startDate.setCaption("начало отсчета");
		startDate.setDateFormat(sdf.toPattern());
		startDate.setLocale(myLocale);

		try {
			startDate.setValue(sdf.parse("2016-10-06 00:00:00 "));
		} catch (ParseException e) {
			System.out.println(e);
		}

		DateField endDate = new DateField();
		endDate.setRequired(true);
		endDate.setResolution(Resolution.SECOND);
		endDate.setCaption("окончание отсчета");
		endDate.setDateFormat(sdf.toPattern());
		endDate.setLocale(myLocale);

		try {
			endDate.setValue(sdf.parse("2016-10-06 00:00:20 "));
		} catch (ParseException e) {
			System.out.println(e);
		}

		ListSelect selectClient = new ListSelect("идентификатор абонента");
		selectClient.setRequired(true);
		selectClient.addItems(MyDao.getClientIDs());
		selectClient.setRows(5);
		selectClient.setMultiSelect(false);
		selectClient.setNullSelectionAllowed(false);
		if (selectClient.getItemIds().size() > 0)
			selectClient.setValue(selectClient.getItemIds().iterator().next());
		//selectClient.setItemCaptionMode(ItemCaptionMode.ITEM);
		selectClient.setLocale(new Locale("fr")); //No comma separators for thousands group
		
		
		ComboBox selectDirection = new ComboBox("направление");
		selectDirection.setRequired(true);
		selectDirection.setNullSelectionAllowed(false);
		selectDirection.addItems(DataDirection.UPLINK, DataDirection.DOWNLINK);
		selectDirection.setValue(DataDirection.UPLINK);

		Label ResultLabel = new Label();

		Button button = new Button("Расcчитать трафик и пропускную способность");
		button.addClickListener(e -> {

			boolean valid = true;
			String result = "";

			if (selectClient.getItemIds().size() == 0) {
				result += "В базе нет записей. ";
				valid = false;
			}
			if (!startDate.isValid()) {
				result += "Необходимо корректно указать начало отсчета. ";
				valid = false;
			}
			if (!endDate.isValid()) {
				result += "Необходимо корректно указать конец отсчета.";
				valid = false;
			}
			if (valid && !startDate.getValue().before(endDate.getValue())) {
				result += "Начало отсчета должно быть раньше конца отсчета";
				valid = false;
			}

			if (valid) {
				long traffic = MyDao.getTraffic((long) selectClient.getValue(), startDate.getValue(),
						endDate.getValue(), (DataDirection) selectDirection.getValue());

				result = "Объем трафика переданного в направлении "
						+ selectDirection.getValue().toString().toLowerCase() + "\nс "
						+ sdf.format(startDate.getValue()) + " по " + sdf.format(endDate.getValue())
						+ "\nабонентом с идентификатором " + selectClient.getValue() + " " + "составляет " + traffic
						+ " байт. Пропускная способность (средняя) составила "
						+ String.format( "%,.2f",8*(float) traffic / ((endDate.getValue().getTime() - startDate.getValue().getTime()) / 1000))
						+ " бит/с. Пропускная способность (пиковая) составила "
						+ String.format( "%,.2f",8*(float)MyDao.getMaxSpeed((long) selectClient.getValue(), startDate.getValue(),
								endDate.getValue(), (DataDirection) selectDirection.getValue()))
						+ " бит/с.";


			}
			ResultLabel.setValue(result);
		});

		layout.addComponents(startDate, endDate, selectClient, selectDirection, button, ResultLabel);
		layout.setMargin(true);
		layout.setSpacing(true);

		setContent(layout);
	}

	@WebListener
	public static class MyListener extends ContextLoaderListener {
	}

	@Configuration
	@EnableVaadin
	public static class MyConfiguration {
	}

	@WebServlet(urlPatterns = "/*", name = "javatest2UIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = Javatest2UI.class, productionMode = false)
	public static class Javatest2UIServlet extends SpringVaadinServlet {
	}
}
