package chen.async.write;

public class TrieTreeWriteEntry {

	private final long id;
	private final long parentId;
	private final String str;
	private final long count;
	private final boolean isValid;

	public TrieTreeWriteEntry(final long id, final long parentId,
			final String ch, final long count, final boolean isValid) {
		this.id = id;
		this.parentId = parentId;
		this.str = ch;
		this.count = count;
		this.isValid = isValid;
	}
	
	public long getId(){
		return this.id;
	}
	
	public long getParentId(){
		return this.parentId;
	}
	
	public String getStr(){
		return this.str;
	}
	
	public long getCount(){
		return this.count;
	}
	
	public boolean getIsValid(){
		return this.isValid;
	}
	
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		return strBuilder.append("The TrieTree Node info: ID is ").append(id)
				.append(" ParentID is ").append(this.parentId)
				.append(" Character is ").append(this.str)
				.append(" Total Number of Count is ").append(this.count)
				.append(" IsValid : ").append(this.isValid).toString();
	}

	@Override
	public boolean equals(Object b) {
		if (b == null || getClass() != b.getClass()) {
			return false;
		}
		if (b == this) {
			return true;
		}
		TrieTreeWriteEntry bTrie = (TrieTreeWriteEntry) b;
		if (bTrie.id == this.id && bTrie.parentId == this.parentId
				& bTrie.str == this.str & bTrie.count == this.count
				& bTrie.isValid == isValid) {
			return true;
		} else {
			return false;
		}
	}
}
