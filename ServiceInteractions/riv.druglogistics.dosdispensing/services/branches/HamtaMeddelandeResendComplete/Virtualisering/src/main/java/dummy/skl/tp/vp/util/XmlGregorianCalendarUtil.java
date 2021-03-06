package dummy.skl.tp.vp.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XmlGregorianCalendarUtil {

	/**
	 * Creates an XMLGregorianCalendar representing current time.
	 * @return
	 */
	public static final XMLGregorianCalendar getNowAsXMLGregorianCalendar() {
		GregorianCalendar now = (GregorianCalendar) GregorianCalendar.getInstance();
		XMLGregorianCalendar xmlNow;
		try {
			xmlNow = DatatypeFactory.newInstance().newXMLGregorianCalendar(now);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(
					"Creation of XMLGregorianCalendar failed", e);
		}
		return xmlNow;
	}
	
	public static final XMLGregorianCalendar fromDate(Date date) {
		Calendar theDate = Calendar.getInstance();
		theDate.setTime(date);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(
					theDate.get(Calendar.YEAR), 
					theDate.get(Calendar.MONTH) + 1, 
					theDate.get(Calendar.DATE), 
					theDate.get(Calendar.HOUR), 
					theDate.get(Calendar.MINUTE), 
					theDate.get(Calendar.SECOND),
					theDate.get(Calendar.MILLISECOND),
					DatatypeConstants.FIELD_UNDEFINED);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(
					"Creation of XMLGregorianCalendar from date failed", e);
		}
	}
}
