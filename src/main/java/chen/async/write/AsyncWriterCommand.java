package chen.async.write;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chen.async.write.IDataItem.Status;

public class AsyncWriterCommand implements ICommand {

	private final IWriter writer;
	private final IDataItem data;
	private static Logger log = LoggerFactory.getLogger(AsyncWriterCommand.class);
	
	public AsyncWriterCommand(final IWriter writer, final IDataItem data){
		this.writer = writer;
		this.data = data;
	}
	
	@Override
	public void execute() {
			// Thread.sleep(100);
		synchronized (this) {
			if (data.getStatus() == Status.CANCELLED) {
				return;
			}
			data.setStatus(Status.RUNNING);
		}
		writer.write(data);
		data.setStatus(Status.DONE);
	}


	@Override
	public synchronized Status getStatus() {
		return data.getStatus();
	}

	@Override
	public synchronized void setStatus(Status status) {
		data.setStatus(status);
	}

}
