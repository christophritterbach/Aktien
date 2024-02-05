package de.ritterbach.jameica.aktien.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.ritterbach.jameica.aktien.rmi.V_Dividende;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class DividendeDeleteAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Dividende dividende = null;
		if (context != null && context instanceof Dividende)
			dividende = (Dividende) context;
		else if ((context != null && context instanceof V_Dividende))
			try {
				dividende = ((V_Dividende) context).getDividende();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		if (context == null)
			throw new ApplicationException(Settings.i18n().tr("please choose a dividende"));
		try {
			// before deleting the abschlag, we show up a confirm dialog ;)
			String question = Settings.i18n().tr("Do you really want to delete this dividende?");
			if (!Application.getCallback().askUser(question))
				return;
			dividende.delete();
			// Send Status update message
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					Settings.i18n().tr("dividende deleted successfully"), StatusBarMessage.TYPE_SUCCESS));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			Logger.error("error while deleting dividende", e);
			throw new ApplicationException(Settings.i18n().tr("error while deleting dividende"));
		}
	}
}
