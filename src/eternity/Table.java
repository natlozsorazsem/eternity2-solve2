package eternity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Table {
	private int dim;
	private TableElem[][] te;
	private Relations rel = new Relations();

	private final int TOP = 0;
	private final int RIGHT = 1;
	private final int BOTTOM = 2;
	private final int LEFT = 3;

	public Table(int d) {
		dim = d;
		te = new TableElem[d][d];

		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				te[i][j] = new TableElem();
			}
		}
	}

	public void initial(int row, int col, Set start) {
		te[row][col].addPotentials(start);
	}

	public void setRelations(HashMap hm) {
		rel.createRelations(hm);
	}

	public boolean fitElem(int row, int col, Puzzle elem) {
		if (elem == null)
			return false;

		Puzzle top = null;
		Puzzle left = null;
		try {
			top = te[row - 1][col].getPuzzle();
		} catch (Exception e) {
			top = new Puzzle(0, new Integer[] { 0, 0, 0, 0 });
		}
		try {
			left = te[row][col - 1].getPuzzle();
		} catch (Exception e) {
			left = new Puzzle(0, new Integer[] { 0, 0, 0, 0 });
		}

		for (int i = 0; i < elem.getElem().length; i++) {
			/*
			 * System.out.println("elem: " + new
			 * ArrayList(Arrays.asList(elem.getElem())));
			 * System.out.println("top: " + new
			 * ArrayList(Arrays.asList(top.getElem())));
			 * System.out.println("left: " + new
			 * ArrayList(Arrays.asList(left.getElem())));
			 * System.out.print(elem.getElemSide(TOP) + "=" +
			 * top.getElemSide(BOTTOM) + " && " + elem.getElemSide(LEFT) + "=" +
			 * left.getElemSide(RIGHT));
			 */
			if (elem.getElemSide(TOP).equals(top.getElemSide(BOTTOM))
					&& elem.getElemSide(LEFT).equals(left.getElemSide(RIGHT))) {
				return true;
			} else {
				elem.rotate();
			}
		}

		return false;
	}

	public void putElem(int row, int col, Puzzle elem) {
		te[row][col].setPuzzle(elem);

		if ((row < dim - 1)) {
			if (isFixed(row + 1, col)) {
				Integer id = te[row + 1][col].getPuzzle().getID();
				te[row + 1][col].addPotential(id);
			} else {
				te[row + 1][col].addPotentials(rel.getPotential(elem.getID(), elem.getElemSide(BOTTOM)));
			}
		}
		if ((col < dim - 1)) {
			if (isFixed(row, col + 1)) {
				Integer id = te[row][col + 1].getPuzzle().getID();
				te[row][col + 1].addPotential(id);
			} else {
				te[row][col + 1].addPotentials(rel.getPotential(elem.getID(), elem.getElemSide(RIGHT)));
			}
		}
	}

	public void setFixElem(int row, int col, Puzzle elem) {
		putElem(row, col, elem);
		te[row][col].setFixed(true);

		if (((col == 0) && (row > 1)) || ((col > 0) && (row > 0))) {
			te[row - 1][col].addPotentials(rel.getPotential(elem.getID(), elem.getElemSide(TOP)));
		}
		if (((row == 0) && (col > 1)) || ((row > 0) && (col > 0))) {
			te[row][col - 1].addPotentials(rel.getPotential(elem.getID(), elem.getElemSide(LEFT)));
		}
	}

	public Puzzle getElem(int row, int col) {
		return te[row][col].getPuzzle();
	}

	public void removeElem(int row, int col) {
		te[row][col].removePuzzle();

		if (row < dim - 1) {
			te[row + 1][col].clearPotential();
			if (isFixed(row + 1, col)) {
				Integer id = te[row + 1][col].getPuzzle().getID();
				te[row + 1][col].addPotential(id);
			}
		}
		if (col < dim - 1) {
			te[row][col + 1].clearPotential();
			if (isFixed(row, col + 1)) {
				Integer id = te[row][col + 1].getPuzzle().getID();
				te[row][col + 1].addPotential(id);
			}
		}

		if ((row > 0) && (col < dim - 1)) {
			Puzzle tmp = te[row - 1][col + 1].getPuzzle();
			te[row][col + 1].addPotentials(rel.getPotential(tmp.getID(), tmp.getElemSide(BOTTOM)));
			tmp = null;
		}
	}

	public Set getPotentials(int row, int col) {
		return te[row][col].getPotentials();
	}

	public Set getElemPotentialTOP(int row, int col) {
		Puzzle tmp = te[row][col].getPuzzle();
		return rel.getPotential(tmp.getID(), tmp.getElemSide(TOP));
	}

	public Set getElemPotentialRIGHT(int row, int col) {
		Puzzle tmp = te[row][col].getPuzzle();
		return rel.getPotential(tmp.getID(), tmp.getElemSide(RIGHT));
	}

	public Set getElemPotentialBOTTOM(int row, int col) {
		Puzzle tmp = te[row][col].getPuzzle();
		return rel.getPotential(tmp.getID(), tmp.getElemSide(BOTTOM));
	}

	public Set getElemPotentialLEFT(int row, int col) {
		Puzzle tmp = te[row][col].getPuzzle();
		return rel.getPotential(tmp.getID(), tmp.getElemSide(LEFT));
	}

	public void addCheckedElem(int row, int col, Integer elem) {
		te[row][col].addChecked(elem);
	}

	public Set getChecked(int row, int col) {
		return te[row][col].getChecked();
	}

	public void clearCheckedElem(int row, int col) {
		te[row][col].clearChecked();
	}

	public boolean isFixed(int row, int col) {
		try {
			return te[row][col].getFixed();
		} catch (Exception e) {
			return false;
		}
	}
}
