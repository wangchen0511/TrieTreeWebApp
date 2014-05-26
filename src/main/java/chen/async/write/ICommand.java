package chen.async.write;

import chen.async.write.IDataItem.Status;

public interface ICommand {

	public void execute();
	
	public Status getStatus();
	
	public void setStatus(Status status);
	
}
