package chen.async.write;

public class TrieTreeSQLDataItem implements IDataItem {

	private final TrieTreeWriteEntry data;
	private IDataItem.Status status = IDataItem.Status.WAITING;
	
	public TrieTreeSQLDataItem(final TrieTreeWriteEntry data){
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return this.data;
	}

	@Override
	public synchronized Status getStatus() {
		return this.status;
	}

	@Override
	public synchronized void setStatus(final Status status) {
		this.status = status;
	}

}
