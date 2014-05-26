package chen.trie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TrieNode {

	private final long id;
	private final long parent;
	private final char ch;
	private Map<Character, TrieNode> map = new HashMap<Character, TrieNode>();
	private AtomicBoolean isValidEnd = new AtomicBoolean(false);;
	private AtomicLong counter = new AtomicLong(0);
	
	public TrieNode(final char ch, final boolean isValidEnd, final long id, final long parent){
		this.ch = ch;
		this.isValidEnd.set(isValidEnd);
		this.id = id;
		this.parent = parent;
	}
	
	public long getID(){
		return this.id;
	}
	
	public char getChar(){
		return this.ch;
	}
	
	public long getParent(){
		return this.parent;
	}
	
	public void setValidEnd(final boolean end){
		this.isValidEnd.set(end);;
	}
	
	public boolean isValidEnd(){
		return this.isValidEnd.get();
	}
	
	public void increaseCount(){
		counter.incrementAndGet();
	}
	
	public long getCount(){
		return counter.get();
	}
	
	public void setCount(long newCount){
		counter.set(newCount);
	}
	
	public TrieNode getChild(final char c){
		return map.get(c);
	}
	
	public void putNewChild(final char c, final TrieNode node){
		if(map.containsKey(c)){
			throw new IllegalArgumentException("The child char " + c + " already exist!");
		}
		map.put(c, node);
	}
	
	public Iterator<Map.Entry<Character, TrieNode>> getAllChildren(){
		return map.entrySet().iterator();
	}
}
