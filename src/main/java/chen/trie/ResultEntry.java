package chen.trie;

public class ResultEntry implements Comparable<ResultEntry>{
	
	private final String str;
	private final long counter;
	
	public ResultEntry(final String str, final long counter){
		this.str = str;
		this.counter = counter;
	}
	
	public String getStr(){
		return this.str;
	}
	
	public long getCounter(){
		return this.counter;
	}

	@Override
	public int compareTo(ResultEntry o) {
		long temp = this.counter - o.counter;
		if(temp > 0){
			return -1;
		} else if (temp == 0){
			return 0;
		} else{
			return 1;
		}
	}
	
}
