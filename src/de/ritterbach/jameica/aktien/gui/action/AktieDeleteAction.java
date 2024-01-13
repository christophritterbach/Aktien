package de.ritterbach.jameica.aktien.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AktieDeleteAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Aktie aktie = null;
		if (context != null && (context instanceof Aktie))
			aktie = (Aktie) context;
		else if (context != null && (context instanceof V_Aktie))
			try {
				aktie = ((V_Aktie) context).getAktie();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		if (aktie == null)
			throw new ApplicationException(Settings.i18n().tr("Please choose a aktie"));
		try {
			// before deleting the aktie, we show up a confirm dialog ;)
			String question = Settings.i18n().tr("Do you really want to delete this aktie?");
			if (!Application.getCallback().askUser(question))
				return;
			aktie.delete();
			// Send Status update message
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					Settings.i18n().tr("aktie deleted successfully"), StatusBarMessage.TYPE_SUCCESS));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			Logger.error("error while deleting aktie", e);
			throw new ApplicationException(Settings.i18n().tr("error while deleting aktie"));
		}
	}
}
