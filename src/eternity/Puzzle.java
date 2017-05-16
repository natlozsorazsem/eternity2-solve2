package eternity;

import java.util.Arrays;
import java.util.Collections;

public class Puzzle {
	private Integer id;
	private byte rotate = 0;
	private Integer puzzle[] = null;

	public Puzzle(Integer key, Integer[] elem) {
		id = key;
		puzzle = elem;
	}

	public Integer getID() {
		return id;
	}

	public Integer[] getElem() {
		return puzzle;
	}

	public Integer getElemSide(int nr) {
		return puzzle[nr];
	}

	public void rotate() {
		rotate = (rotate < puzzle.length - 1) ? ++rotate : 0;
		Collections.rotate(Arrays.asList(puzzle), -1);
	}
}