package de.ritterbach.jameica.aktien.formatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import de.willuhn.jameica.gui.formatter.Formatter;

public class DecimalFormatter implements Formatter {

	private DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());

	public DecimalFormatter(DecimalFormat formatter) {
		if (formatter != null)
			this.formatter = formatter;
	}

	@Override
	public String format(Object o) {
	    if (o == null)
	        return "";
	      if (o instanceof Number)
	        return (formatter.format(((Number)o).doubleValue()));
	      return o.toString();
	}

}
