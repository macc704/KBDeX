package checker;

import java.util.List;

import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;

public class ContainsChecker {
	public static void main(String[] args) {
		new ContainsChecker().run();
	}

	void run() {
		CFile file = CFileSystem.getExecuteDirectory().findFile("word.txt");
		List<String> words = file.loadTextAsList();
		for (String w1 : words) {
			for (String w2 : words) {
				if (!w1.equals(w2) && w1.contains(w2)) {
					System.out.println("\"" + w1 + "\"" + " contains " + "\""
							+ w2 + "\"");
				}
			}
		}
	}
}
