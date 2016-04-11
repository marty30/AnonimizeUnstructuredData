package nl.willemsenmedia.utwente.anonymization.data;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Martijn on 8-3-2016.
 * <p>
 * All data attributes. An attribute contains a type and the real data.
 * Each field in a form would correspond to a DataAttribute.
 */
public class DataAttribute implements Cloneable {
	private DataType dataType;
	private String data;
	private String name;
	private boolean doAnonimize = true;
	private ReadWriteLock rwlock = new ReentrantReadWriteLock();

	public DataAttribute(DataType dataType, String data) {
		this(dataType, null, data, true);
	}

	public DataAttribute(DataType dataType, String name, String data, boolean doAnonimize) {
		this.dataType = dataType;
		this.name = name;
		this.data = data.trim();
		this.doAnonimize = doAnonimize;
	}

	public DataType getDataType() {
		return dataType;
	}

	public String getData() {
		rwlock.writeLock().lock();
		try {
			return data;
		} finally {
			rwlock.writeLock().unlock();
		}
	}

	public void setData(String data) {
		rwlock.readLock().lock();
		try {
			this.data = data.trim();
		} finally {
			rwlock.readLock().unlock();
		}
	}

	public boolean doAnonimize() {
		return doAnonimize;
	}

	public void setDoAnonimize(boolean val) {
		doAnonimize = val;
	}

	@Override
	public String toString() {
		if (name != null)
			return name + ": " + data + " (" + dataType.toString() + ")";
		else
			return dataType.toString() + ": " + data;
	}

	@Override
	public DataAttribute clone() {
		return new DataAttribute(this.getDataType(), this.name, this.getData(), this.doAnonimize);
	}
}
