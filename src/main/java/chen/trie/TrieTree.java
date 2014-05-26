package chen.trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TrieTree implements ITrieTree{

	private AtomicLong id;
	private final TrieNode root;
	final ReadWriteLock lock = new ReentrantReadWriteLock();

	public TrieTree(final TrieNode root, final long count){
		this.root = root;
		id = new AtomicLong(count);
	}
	
	public TrieTree(){
		this(new TrieNode('.', false, 0, 0), 1);
	}
	
	public boolean isExist(String str, boolean addCount, boolean setVaildWord) {
		str = str.trim();
		TrieNode searchNode = this.root;
		lock.readLock().lock();
		try {
			for (int i = 0; i < str.length(); i++) {
				searchNode = searchNode.getChild(str.charAt(i));
				if (searchNode == null) {
					return false;
				}
			}
		} finally {
			lock.readLock().unlock();
		}
		if (addCount) {
			searchNode.increaseCount();
		}
		if (setVaildWord) {
			searchNode.setValidEnd(true);
		}
		return true;
	}
	
	long getIdAndIncrement(){
		return id.getAndIncrement();
	}

	/*
	 * Later we can have similar words match in case our users have some typos.
	 */
	public List<ResultEntry> matchedStr(String str) {
		str = str.trim();
		List<ResultEntry> res = new ArrayList<ResultEntry>();
		TrieNode searchNode = this.root;
		lock.readLock().lock();
		try {
			for (int i = 0; i < str.length(); i++) {
				searchNode = searchNode.getChild(str.charAt(i));
				if (searchNode == null) {
					break;
				}
			}
			if (searchNode == null) {
				return res;
			}
			StringBuilder strBuilder = new StringBuilder(str);
			getAllMatched(res, strBuilder, searchNode);
		} finally {
			lock.readLock().unlock();
		}
		Collections.sort(res);
		return res;
	}

	TrieNode getRoot(){
		return this.root;
	}
	
	private void getAllMatched(List<ResultEntry> res, StringBuilder strBuilder,
			TrieNode startNode) {
		if (startNode.isValidEnd()) {
			res.add(new ResultEntry(strBuilder.toString(), startNode.getCount()));
		}
		Iterator<Map.Entry<Character, TrieNode>> children = startNode
				.getAllChildren();
		while (children.hasNext()) {
			Map.Entry<Character, TrieNode> entry = children.next();
			getAllMatched(res, strBuilder.append(entry.getKey()),
					entry.getValue());
			strBuilder.deleteCharAt(strBuilder.length() - 1);
		}
	}

	public TrieNode putStr(String str) {
		str = str.trim();
		if (isExist(str, true, true)) {
			return null;
		}
		TrieNode currentNode = this.root;
		TrieNode prevNode = null;
		int index = 0;
		lock.writeLock().lock();
		try {
			for (; index < str.length(); index++) {
				prevNode = currentNode;
				currentNode = prevNode.getChild(str.charAt(index));
				if (currentNode == null) {
					currentNode = new TrieNode(str.charAt(index), false, getIdAndIncrement(), prevNode.getID());
					prevNode.putNewChild(str.charAt(index), currentNode);
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
		currentNode.setValidEnd(true);
		currentNode.increaseCount();
		return currentNode;
	}

}
