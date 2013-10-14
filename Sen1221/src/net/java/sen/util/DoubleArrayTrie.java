package net.java.sen.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DoubleArrayTrie {
	private final static int BUF_SIZE = 500000;
	private static Log log = LogFactory.getLog(DoubleArrayTrie.class);

	private int array[];
	private int used[];
	private int size;

	private int alloc_size;
	private char str[][];
	private int str_size;
	private int len[];
	private int val[];

	private int next_check_pos;
	@SuppressWarnings("unused")
	private int no_delete;

	private class Node {
		int code;
		int depth;
		int left;
		int right;
	};

	public DoubleArrayTrie() {
		array = null;
		used = null;
		size = 0;
		alloc_size = 0;
		no_delete = 0;
	}

	public void load(String fileName) throws IOException {
		log.info("loading double array trie dict = " + fileName);
		long start = System.currentTimeMillis();
		File file = new File(fileName);
		array = new int[(int) (file.length() / 4)];
		DataInputStream is = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file), BUF_SIZE));
		for (int i = 0; i < array.length; i++) {
			array[i] = is.readInt();
		}
		log.info("loaded time = "
				+ (((double) (System.currentTimeMillis() - start)) / 1000)
				+ "[ms]");
	}

	int[] _resize(int ptr[], int n, int l, int v) {
		int tmp[] = new int[l];
		if (ptr != null) {
			l = ptr.length;
		} else {
			l = 0;
		}

		for (int i = 0; i < l; i++)
			tmp[i] = ptr[i];
		for (int i = l; i < l; i++)
			tmp[i] = v;
		ptr = null;
		return tmp;
	}

	int resize(int new_size) {
		array = _resize(array, alloc_size << 1, new_size << 1, (int) 0);
		used = _resize(used, alloc_size, new_size, (int) 0);

		alloc_size = new_size;

		return new_size;
	}

	int fetch(Node parent, Vector siblings) {
		int prev = 0;
		if (log.isTraceEnabled()) {
			log.trace("parent.left=" + parent.left);
			log.trace("parent.right=" + parent.right);
			log.trace("parent.depth=" + parent.depth);
		}

		for (int i = parent.left; i < parent.right; i++) {
			if (((len != null) ? len[i] : str[i].length) < parent.depth)
				continue;

			char tmp[] = str[i];

			int cur = 0;
			if (((len != null) ? len[i] : str[i].length) != parent.depth) {
				if (log.isTraceEnabled())
					log.trace("tmp[" + parent.depth + "]=" + tmp[parent.depth]);
				cur = (int) tmp[parent.depth] + 1;
			}

			if (prev > cur) {
				log.error("given strings are not sorted.\n");
				throw new RuntimeException(
						"Fatal: given strings are not sorted.\n");
			}

			if (cur != prev || siblings.size() == 0) {
				Node tmp_node = new Node();
				tmp_node.depth = parent.depth + 1;
				tmp_node.code = cur;
				tmp_node.left = i;
				if (siblings.size() != 0)
					((Node) siblings.get(siblings.size() - 1)).right = i;

				siblings.add(tmp_node);
			}

			prev = cur;
		}

		if (siblings.size() != 0)
			((Node) siblings.get(siblings.size() - 1)).right = parent.right;

		return siblings.size();
	}

	int insert(Vector siblings) {
		int begin = 0;
		int pos = (((((Node) siblings.get(0)).code + 1) > ((int) next_check_pos)) ? (((Node) siblings
				.get(0)).code + 1)
				: ((int) next_check_pos)) - 1;

		int nonzero_num = 0;
		int first = 0;

		while (true) {
			pos++;
			{
				int t = (int) (pos);
				if (t > alloc_size) {
					resize((int) (t * 1.05));
				}
			}
			;

			if (array[(((int) pos) << 1) + 1] != 0) {
				nonzero_num++;
				continue;
			} else if (first == 0) {
				next_check_pos = pos;
				first = 1;
			}

			begin = pos - ((Node) siblings.get(0)).code;

			{
				int t = (int) (begin + ((Node) siblings
						.get(siblings.size() - 1)).code);
				if (t > alloc_size) {
					resize((int) (t * 1.05));
				}
			}
			;

			if (used[begin] != 0)
				continue;

			boolean flag = false;

			for (int i = 1; i < siblings.size(); i++) {
				if (array[(((int) begin + ((Node) siblings.get(i)).code) << 1) + 1] != 0) {
					flag = true;
					break;
				}
			}
			if (!flag)
				break;
		}

		if (1.0 * nonzero_num / (pos - next_check_pos + 1) >= 0.95)
			next_check_pos = pos;
		used[begin] = 1;
		size = (((size) > ((int) begin
				+ ((Node) siblings.get(siblings.size() - 1)).code + 1)) ? (size)
				: ((int) begin
						+ ((Node) siblings.get(siblings.size() - 1)).code + 1));

		for (int i = 0; i < siblings.size(); i++) {
			array[(((int) begin + ((Node) siblings.get(i)).code) << 1) + 1] = begin;
		}

		for (int i = 0; i < siblings.size(); i++) {
			Vector new_siblings = new Vector();

			if (fetch(((Node) siblings.get(i)), new_siblings) == 0) {
				array[((int) begin + (int) ((Node) siblings.get(i)).code) << 1] = (val != null) ? (int) (-val[((Node) siblings
						.get(i)).left] - 1)
						: (int) (-((Node) siblings.get(i)).left - 1);

				if ((val != null)
						&& ((int) (-val[((Node) siblings.get(i)).left] - 1) >= 0)) {
					log.error("negative value is assgined.");
					throw new RuntimeException(
							"Fatal: negative value is assgined.");
				}

			} else {
				int ins = (int) insert(new_siblings);
				array[((int) begin + ((Node) siblings.get(i)).code) << 1] = ins;
			}
		}

		return begin;
	}

	void clear() {
		array = null;
		used = null;
		alloc_size = 0;
		size = 0;
		no_delete = 0;
	}

	int get_unit_size() {
		return 8;
	};

	int get_size() {
		return size;
	};

	int get_nonzero_size() {
		int result = 0;
		for (int i = 0; i < size; i++)
			if (array[(((int) i) << 1) + 1] != 0)
				result++;
		return result;
	}

	public int build(char _str[][], int _len[], int _val[])

	{
		return build(_str, _len, _val, _str.length);
	}

	public int build(char _str[][], int _len[], int _val[], int size) {
		if (_str == null)
			return 0;
		if (_str.length != _val.length) {
			log.warn("index and text should be same size.");
			return 0;
		}

		str = _str;
		len = _len;
		str_size = size;
		val = _val;

		resize(1024 * 10);

		array[((int) 0) << 1] = 1;
		next_check_pos = 0;

		Node root_node = new Node();
		root_node.left = 0;
		root_node.right = str_size;
		root_node.depth = 0;

		Vector siblings = new Vector();
		log.trace("---fetch---");
		fetch(root_node, siblings);
		log.trace("---insert---");
		insert(siblings);

		used = null;

		return size;
	}

	public int search(char key[], int pos, int len) {
		if (len == 0)
			len = key.length;

		int b = array[((int) 0) << 1];
		int p;
		for (int i = pos; i < len; i++) {
			p = b + (char) (key[i]) + 1;
			if ((int) b == array[(((int) p) << 1) + 1])
				b = array[((int) p) << 1];
			else
				return -1;
		}

		p = b;
		int n = array[((int) p) << 1];

		if ((int) b == array[(((int) p) << 1) + 1] && n < 0)
			return (int) (-n - 1);
		return -1;
	}

	public int commonPrefixSearch(char key[], int result[], int pos, int len) {
		if (len == 0)
			len = key.length;

		int b = array[((int) 0) << 1];
		int num = 0;
		int n;
		int p;

		for (int i = pos; i < len; i++) {

			p = b;
			n = array[((int) p) << 1];
			if ((int) b == array[(((int) p) << 1) + 1] && n < 0) {
				if (log.isTraceEnabled())
					log.trace("result[" + num + "]=" + (-n - 1));
				if (num < result.length) {
					result[num] = -n - 1;
				} else {
					log.warn("result array size may not enough");
				}
				num++;
			}

			p = b + (char) (key[i]) + 1;

			if ((p << 1) > array.length) {
				log.warn("p range is over.");
				log.warn("(p<<1,array.length)=(" + (p << 1) + ","
						+ array.length + ")");
				return num;
			}

			if ((int) b == array[(((int) p) << 1) + 1]) {
				b = array[((int) p) << 1];
			} else {
				return num;
			}
		}

		p = b;
		n = array[((int) p) << 1];
		if ((int) b == array[(((int) p) << 1) + 1] && n < 0) {
			if (log.isTraceEnabled())
				log.trace("result[" + num + "]=" + (-n - 1));
			if (num < result.length) {
				result[num] = -n - 1;
			} else {
				log.warn("result array size may not enough");
			}
			num++;
		}

		return num;
	}

	public void save(String file) throws IOException {
		long start = System.currentTimeMillis();
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)));
		int dsize = alloc_size << 1;
		for (int i = 0; i < dsize; i++) {
			out.writeInt(array[i]);
		}
		out.close();
		log.info("save time = "
				+ (((double) (System.currentTimeMillis() - start)) / 1000)
				+ "[s]");
	}

	public static void dumpChar(char c[], String message) {
		System.err.println("message=" + message);
		for (int i = 0; i < c.length; i++) {
			System.err.print(c[i] + ",");
		}
		System.err.println();
	}

	public static void main(String args[]) {
	}
}
