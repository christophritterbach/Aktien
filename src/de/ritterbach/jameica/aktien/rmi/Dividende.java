package de.ritterbach.jameica.aktien.rmi;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBObject;

public interface Dividende extends DBObject {
	public Aktie getAktie() throws RemoteException;
	public void setAktie(Aktie aktie) throws RemoteException;
	public Date getZahlDatum() throws RemoteException;
	public void setZahlDatum(Date zahlDatum) throws RemoteException;
	public BigDecimal getProStueck() throws RemoteException;
	public void setProStueck(BigDecimal proStueck) throws RemoteException;
	public BigDecimal getGesamt() throws RemoteException;
	public void setGesamt(BigDecimal gesamt) throws RemoteException;
	public BigDecimal getQuellensteuer() throws RemoteException;
	public void setQuellensteuer(BigDecimal quellensteuer) throws RemoteException;
	public BigDecimal getDevisenkurs() throws RemoteException;
	public void setDevisenkurs(BigDecimal devisenkurs) throws RemoteException;
	public String getWaehrung() throws RemoteException;
	public void setWaehrung(String waehrung) throws RemoteException;
}
