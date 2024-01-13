package de.ritterbach.jameica.aktien.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class AktieDetailAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Aktie aktie = null;

		if (context != null && (context instanceof Aktie)) {
			aktie = (Aktie) context;
		} else if (context != null && (context instanceof V_Aktie)) {
			V_Aktie v_aktie = (V_Aktie)context;
			try {
				DBIterator<Aktie> iter = Settings.getDBService().createList(Aktie.class);
				iter.addFilter("id=?", v_aktie.getID());
				if (iter.hasNext())
					aktie = iter.next();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			throw new ApplicationException(Settings.i18n().tr("error while creating new aktie"));
		}
		GUI.startView(de.ritterbach.jameica.aktien.gui.views.details.Aktie.class.getName(), aktie);
	}

}
