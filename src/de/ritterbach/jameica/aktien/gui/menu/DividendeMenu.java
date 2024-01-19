package de.ritterbach.jameica.aktien.gui.menu;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.action.DividendeDeleteAction;
import de.ritterbach.jameica.aktien.gui.action.DividendeDetailAction;
import de.ritterbach.jameica.aktien.gui.action.DividendeNewAction;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.util.ApplicationException;

public class DividendeMenu extends ContextMenu {
	public DividendeMenu(Aktie aktie) {
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Open..."),new DividendeDetailAction()));
		// separator
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new ContextMenuItem(Settings.i18n().tr("New..."),new Action()
		{
			public void handleAction(Object context) throws ApplicationException
			{
				new DividendeNewAction().handleAction(aktie);
			}
		}));
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Delete..."),new DividendeDeleteAction()));
	}
}
