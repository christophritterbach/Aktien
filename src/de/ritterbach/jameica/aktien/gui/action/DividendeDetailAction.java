package de.ritterbach.jameica.aktien.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.ritterbach.jameica.aktien.rmi.V_Dividende;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class DividendeDetailAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Dividende dividende = null;

		if (context != null && (context instanceof Dividende)) {
			dividende = (Dividende) context;
		} else if (context != null && (context instanceof V_Dividende)) {
			V_Dividende vDividende = (V_Dividende)context;
			try {
				DBIterator<Dividende> iter = Settings.getDBService().createList(Dividende.class);
				iter.addFilter("id=?", vDividende.getID());
				if (iter.hasNext())
					dividende = iter.next();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			throw new ApplicationException(Settings.i18n().tr("error while creating new dividende"));
		}
		GUI.startView(de.ritterbach.jameica.aktien.gui.views.details.DividendenView.class.getName(), dividende);
	}

}
