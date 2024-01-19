package de.ritterbach.jameica.aktien.gui.views.details;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabFolder;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.control.details.AktieControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.gui.util.TabGroup;
import de.willuhn.util.ApplicationException;

public class AktieView extends AbstractView {

	public AktieView() {
	}

	@Override
	public void bind() throws Exception {
		// draw the title
		GUI.getView().setTitle(Settings.i18n().tr("Aktie details"));
		// instanciate controller
		final AktieControl control = new AktieControl(this);
		Container cAktie = new SimpleContainer(getParent());
		cAktie.addHeadline(Settings.i18n().tr("Aktie"));
		// layout with 2 columns
		{
			ColumnLayout columns = new ColumnLayout(cAktie.getComposite(), 2);
			// left side
			Container left = new SimpleContainer(columns.getComposite());
			left.addInput(control.getIsin());
			left.addInput(control.getBezeichnung());
			// right side
			Container right = new SimpleContainer(columns.getComposite(), true);
			right.addInput(control.getWkn());
			right.addInput(control.getAnzahl());
		}
		ButtonArea buttons = new ButtonArea();
		buttons.addButton(Settings.i18n().tr("Store"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				control.handleStore();
			}
		}, null, true); // "true" defines this button as the default button
		// Don't forget to paint the button area
		buttons.paint(getParent());
		Container cUmsatz = new SimpleContainer(getParent());
		cUmsatz.addHeadline(Settings.i18n().tr("Umsaetze"));
		{
			ColumnLayout columns = new ColumnLayout(cUmsatz.getComposite(), 2);
			// left side
			Container left = new SimpleContainer(columns.getComposite());
			left.addInput(control.getKauf());
			left.addInput(control.getDividende());
			// right side
			Container right = new SimpleContainer(columns.getComposite(), true);
			right.addInput(control.getKosten());
		}
		
		final TabFolder folder = new TabFolder(getParent(), SWT.NONE);
	    folder.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    TabGroup tg1 = new TabGroup(folder,Settings.i18n().tr("Dividenden"),true,1);
	    TablePart dlp  = control.getDivideneListPart();
	    dlp.paint(tg1.getComposite());
	    
	    final TabGroup tg2 = new TabGroup(folder,Settings.i18n().tr("Kosten"),true,1);
	    TablePart klp  = control.getKaufListPart();
	    klp.paint(tg2.getComposite());
		
	}
}
