package eternity;

import java.util.HashSet;
import java.util.Set;

public class TableElem {
	private boolean fix = false;
	private Puzzle puzzleElem = null;
	private Set<Integer> potentials;
	private Set<Integer> checked;

	public TableElem() {
		checked = new HashSet<Integer>();
		potentials = new HashSet<Integer>();
	}

	public void setPuzzle(Puzzle elem) {
		puzzleElem = elem;
	}

	public Puzzle getPuzzle() {
		return puzzleElem;
	}

	public void removePuzzle() {
		puzzleElem = null;
	}

	public void addPotential(Integer id) {
		potentials.add(id);
	}

	public void addPotentials(Set s) {
		potentials.addAll(s);
	}

	public void clearPotential() {
		potentials.clear();
	}

	public Set getPotentials() {
		return potentials;
	}

	public void addChecked(Integer id) {
		checked.add(id);
	}

	public Set getChecked() {
		return checked;
	}

	public void clearChecked() {
		checked.clear();
	}

	public void setFixed(boolean fixed) {
		fix = fixed;
	}

	public boolean getFixed() {
		return fix;
	}
}
