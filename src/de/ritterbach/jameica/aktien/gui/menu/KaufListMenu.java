package de.ritterbach.jameica.aktien.gui.menu;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.action.AktieDetailAction;
import de.ritterbach.jameica.aktien.gui.action.KaufDeleteAction;
import de.ritterbach.jameica.aktien.gui.action.KaufDetailAction;
import de.ritterbach.jameica.aktien.rmi.V_Kauf;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.util.ApplicationException;

public class KaufListMenu extends ContextMenu {
	public KaufListMenu() {
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Open..."),new KaufDetailAction()));
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Delete..."),new KaufDeleteAction()));
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("show_aktie..."), new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
				if (context instanceof V_Kauf) {
					V_Kauf kauf = (V_Kauf) context;
					try {
						new AktieDetailAction().handleAction(kauf.getAktie());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}));
	}
}
