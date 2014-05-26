package chen.trie;


import java.util.List;

public interface ITrieTree {

	public boolean isExist(String str, boolean addCount, boolean setVaildWord);

	public List<ResultEntry> matchedStr(String str);

	public TrieNode putStr(String str);
	
}
