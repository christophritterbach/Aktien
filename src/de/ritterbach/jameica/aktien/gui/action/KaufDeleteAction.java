package de.ritterbach.jameica.aktien.gui.action;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class KaufDeleteAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		if (context == null || !(context instanceof Kauf))
			throw new ApplicationException(Settings.i18n().tr("please choose a kauf"));
		Kauf kauf = (Kauf) context;
		try {
			// before deleting the abschlag, we show up a confirm dialog ;)
			String question = Settings.i18n().tr("Do you really want to delete this kauf?");
			if (!Application.getCallback().askUser(question))
				return;
			kauf.delete();
			// Send Status update message
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					Settings.i18n().tr("kauf deleted successfully"), StatusBarMessage.TYPE_SUCCESS));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			Logger.error("error while deleting kauf", e);
			throw new ApplicationException(Settings.i18n().tr("error while deleting kauf"));
		}
	}
}
