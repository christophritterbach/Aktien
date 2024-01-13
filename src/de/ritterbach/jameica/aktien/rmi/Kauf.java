package de.ritterbach.jameica.aktien.rmi;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBObject;

public interface Kauf extends DBObject {
	public Aktie getAktie() throws RemoteException;
	public void setAktie(Aktie aktie) throws RemoteException;
	public Date getKaufDatum() throws RemoteException;
	public void setKaufDatum(Date kaufDatum) throws RemoteException;
	public BigDecimal getAnzahl() throws RemoteException;
	public void setAnzahl(BigDecimal anzahl) throws RemoteException;
	public BigDecimal getKurs() throws RemoteException;
	public void setKurs(BigDecimal kurs) throws RemoteException;
	public BigDecimal getBetrag() throws RemoteException;
	public void setBetrag(BigDecimal betrag) throws RemoteException;
	public BigDecimal getKosten() throws RemoteException;
	public void setKosten(BigDecimal kosten) throws RemoteException;
	public String getBemerkung() throws RemoteException;
	public void setBemerkung(String bemerkung) throws RemoteException;
}
