package de.ritterbach.jameica.aktien.gui.menu;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.action.AktieDetailAction;
import de.ritterbach.jameica.aktien.gui.action.DividendeDeleteAction;
import de.ritterbach.jameica.aktien.gui.action.DividendeDetailAction;
import de.ritterbach.jameica.aktien.rmi.V_Dividende;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.util.ApplicationException;

public class DividendeListMenu extends ContextMenu {
	public DividendeListMenu() {
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Open..."),new DividendeDetailAction()));
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Delete..."),new DividendeDeleteAction()));
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("show_aktie..."), new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
				if (context instanceof V_Dividende) {
					V_Dividende div = (V_Dividende) context;
					try {
						new AktieDetailAction().handleAction(div.getAktie());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}));
	}
}
