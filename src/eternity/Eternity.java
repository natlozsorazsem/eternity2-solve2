
package eternity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Eternity {
	private static final int DIM = 4;
	private static final Integer EDGE = new Integer(0);

	private static HashMap<Integer, Puzzle> elemek = new HashMap<Integer, Puzzle>();
	private static List<Integer> cornersElems = new ArrayList<Integer>();
	private static List<Integer> edgesElems = new ArrayList<Integer>();
	private static List<Integer> innerElems = new ArrayList<Integer>();

	private static int step = 0;
	private static int solutionNumber = 0;
	private static Table table = null;

	private static void printSolve2(int num) {
		final String space = "    ";
		final int N = DIM;
		String row = "";

		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				String p = null;
				try {
					p = table.getElem(r, c).getID().toString();
				} catch (NullPointerException e) {
					p = "0";
				}
				String sn = space + p;
				row += sn.substring(p.length(), sn.length());
			}
		}
		System.out.println(row);
	}

	private static void printSolve(int num) {
		final String space = "   ";
		final int N = DIM;
		String sepLine = "+";

		System.out.println(num);
		for (int i = 0; i < N; i++)
			sepLine += "-----+";

		for (int r = 0; r < N; r++) {
			System.out.println(sepLine);
			System.out.print("|");
			for (int c = 0; c < N; c++) {
				String p = null;
				try {
					p = table.getElem(r, c).getID().toString();
				} catch (NullPointerException e) {
					p = "";
				}
				String sn = space + p;
				String s = sn.substring(p.length(), sn.length());
				System.out.print(" " + s + " |");
			}
			System.out.println();
		}
		System.out.println(sepLine);
	}

	public static void calcTime(long timeInMillis) {
		final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));
		int remdr = (int) (timeInMillis % (24L * 60 * 60 * 1000));
		final int hours = remdr / (60 * 60 * 1000);
		remdr %= 60 * 60 * 1000;
		final int minutes = remdr / (60 * 1000);
		remdr %= 60 * 1000;
		final int seconds = remdr / 1000;
		final int ms = remdr % 1000;
		System.out
				.println("elapsed time: " + days + "d  " + hours + "h " + minutes + "m " + seconds + "s " + ms + "ms");
	}

	public static Set difference(Set setA, Set setB) {
		Set tmp = new HashSet(setA);
		if (setB != null && !setB.isEmpty())
			tmp.removeAll(setB);
		return tmp;
	}

	private static void sortingElem(Integer id, Puzzle elem) {
		int edgeNR = 0;
		List<Integer> sides = new ArrayList(Arrays.asList(elem.getElem()));
		for (Iterator itr = sides.iterator(); itr.hasNext();) {
			Integer side = (Integer) itr.next();
			if (side.equals(EDGE)) {
				edgeNR++;
			}
		}

		if (edgeNR == 0) {
			innerElems.add(id);
		} else if (edgeNR == 1) {
			edgesElems.add(id);
		} else {
			cornersElems.add(id);
		}
	}

	private static void sortingElements() {
		Iterator it = elemek.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Integer key = (Integer) entry.getKey();
			Puzzle value = (Puzzle) entry.getValue();

			sortingElem(key, value);
		}
	}

	private static void putBackElem(Puzzle elem) {
		sortingElem(elem.getID(), elem);
	}

	private static Set<Integer> getAvailableElems(int row, int col, List<Integer> list, Set<Integer> pot) {
		Set<Integer> result = new HashSet(list);
		Set<Integer> checked = new HashSet();

		try {
			result.retainAll(pot);

			/* ha alkalmazzuk a fix elem lehetőséget */
			if (row + 1 < DIM) {
				if (table.isFixed(row + 1, col)) {
					result.retainAll(table.getElemPotentialTOP(row + 1, col));
				}
			}
			if (col + 1 < DIM) {
				if (table.isFixed(row, col + 1)) {
					result.retainAll(table.getElemPotentialLEFT(row, col + 1));
				}
			}
			/* eddig */

			checked = table.getChecked(row, col);

			result = difference(result, checked);
		} catch (Exception e) {
			result = null;
		} finally {
			checked = null;
		}

		return result;
	}

	private static void removeInnerElem(Integer id) {
		innerElems.remove(innerElems.indexOf(id));
	}

	private static Puzzle selectElem(int row, int col, Set pot) {
		Integer id = null;
		Puzzle result = null;
		Set<Integer> tmp = new HashSet();

		if (((row == 0) && (col == 0)) || ((row == 0) && (col == DIM - 1)) || ((row == DIM - 1) && (col == 0))
				|| ((row == DIM - 1) && (col == DIM - 1))) {
			if (!cornersElems.isEmpty()) {
				if ((pot != null) && (!pot.isEmpty())) {
					tmp = getAvailableElems(row, col, cornersElems, pot);

					if ((tmp != null) && (!tmp.isEmpty())) {
						id = (Integer) tmp.iterator().next();
						cornersElems.remove(cornersElems.indexOf(id));
					}
				} else {
					id = cornersElems.get(0);
					cornersElems.remove(0);
				}

				if (id != null) {
					result = elemek.get(id);
					table.addCheckedElem(row, col, id);
				}
			}
		} else if (((row == 0) && (col > 0) && (col < DIM - 1)) || ((row == DIM - 1) && (col > 0) && (col < DIM - 1))
				|| ((row > 0) && (row < DIM - 1) && (col == 0)) || ((row > 0) && (row < DIM - 1) && (col == DIM - 1))) {
			if (!edgesElems.isEmpty()) {
				if ((pot != null) && (!pot.isEmpty())) {
					tmp = getAvailableElems(row, col, edgesElems, pot);

					if ((tmp != null) && (!tmp.isEmpty())) {
						id = (Integer) tmp.iterator().next();
						edgesElems.remove(edgesElems.indexOf(id));
					}
				} else {
					id = edgesElems.get(0);
					edgesElems.remove(0);
				}

				if (id != null) {
					result = elemek.get(id);
					table.addCheckedElem(row, col, id);
				}
			}
		} else {
			if (!innerElems.isEmpty()) {
				if ((pot != null) && (!pot.isEmpty())) {
					tmp = getAvailableElems(row, col, innerElems, pot);

					if ((tmp != null) && (!tmp.isEmpty())) {
						id = (Integer) tmp.iterator().next();
						innerElems.remove(innerElems.indexOf(id));
					}
				} else {
					id = innerElems.get(0);
					innerElems.remove(0);
				}

				if (id != null) {
					result = elemek.get(id);
					table.addCheckedElem(row, col, id);
				}
			}
		}

		tmp = null;

		return result;
	}

	public static void startSolve() {
		solve();
	}

	private static int getBackStepCol(int row, int col) {
		int result = --col;

		if (table.isFixed(row, col)) {
			result = getBackStepCol(row, col);
		}

		return result;
	}

	private static void solve() {
		int row = 0; // sor
		int col = 0; // oszlop
		while ((row >= 0) && (row < DIM)) {
			while ((col >= 0) && (col < DIM)) {
				// System.out.print(++step + ". (" + row + "," + col + "): ");

				if (table.isFixed(row, col)) {
					col++;
				} else {
					if (checkPosition(row, col)) {
						col++;
					} else {
						backStep(row, col);

						col = getBackStepCol(row, col);
					}
				}
				// printSolve(++solutionNumber);
				// printSolve2(++solutionNumber);

				if ((col == DIM) && (row == DIM - 1)) {
					printSolve(++solutionNumber);
					// printSolve2(++solutionNumber);
					col--;
					putBackElem(table.getElem(row, col));
					table.removeElem(row, col);
				}
			}
			if (col >= DIM) {
				row++;
				col = 0;
			} else {
				row--;
				col = DIM - 1;
			}
		}
	}

	private static boolean checkPosition(int row, int col) {
		Puzzle p = null;
		Set pot = table.getPotentials(row, col);

		if (pot.size() == 0)
			return false;
		else {
			p = selectElem(row, col, pot);

			if (p == null)
				return false;

			// System.out.print(p.getID() + " => " + new
			// ArrayList(Arrays.asList(p.getElem())) + " check...");

			if (table.fitElem(row, col, p)) {
				// System.out.println("OK => put table " + new
				// ArrayList(Arrays.asList(p.getElem())));
				table.putElem(row, col, p);
				return true;
			} else {
				// System.out.print("notOK | ");
				putBackElem(p);
				return checkPosition(row, col);
			}
		}
	}

	private static Integer[] getPrevIndex(int row, int col) {
		int x = 0; // sor
		int y = 0; // oszlop
		if (col < 1) {
			x = row - 1;
			y = DIM - 1;
		} else {
			x = row;
			y = col - 1;
		}

		Integer result[] = new Integer[] { x, y };

		if (table.isFixed(x, y)) {
			result = getPrevIndex(x, y);
		}

		return result;
	}

	private static void backStep(int row, int col) {
		table.clearCheckedElem(row, col);

		Integer xy[] = getPrevIndex(row, col);

		/*System.out.print("BACKSTEP ");
		try {
			System.out.println(table.getElem(xy[0], xy[1]).getID() + " => "
					+ new ArrayList(Arrays.asList(table.getElem(xy[0], xy[1]).getElem())) + " DELETE");
		} catch (Exception e) {
			System.out.println("=> END");
		}*/

		Puzzle before = null;
		try {
			before = table.getElem(xy[0], xy[1]);
			putBackElem(before);
			table.removeElem(xy[0], xy[1]);
		} catch (Exception e) {
			return;
		} finally {
			xy = null;
			before = null;
		}
	}

	private static void setFixElem(int row, int col, Puzzle elem) {
		table.setFixElem(row, col, elem);
		removeInnerElem(elem.getID());
		table.addCheckedElem(row, col, elem.getID());
	}

	public static void main(String[] args) {
		Integer[] p1 = { 0, 2, 1, 0 };
		Integer[] p2 = { 0, 0, 1, 2 };
		Integer[] p3 = { 2, 0, 0, 2 };
		Integer[] p4 = { 1, 0, 0, 1 };
		Integer[] p5 = { 3, 4, 3, 3 };
		Integer[] p6 = { 4, 4, 3, 4 };
		Integer[] p7 = { 4, 4, 3, 3 };
		Integer[] p8 = { 3, 4, 4, 3 };
		Integer[] p9 = { 1, 4, 1, 0 };
		Integer[] p10 = { 2, 4, 2, 0 };
		Integer[] p11 = { 2, 4, 1, 0 };
		Integer[] p12 = { 1, 4, 2, 0 };
		Integer[] p13 = { 1, 3, 1, 0 };
		Integer[] p14 = { 2, 3, 2, 0 };
		Integer[] p15 = { 1, 3, 2, 0 };
		Integer[] p16 = { 2, 3, 1, 0 };

		elemek.put(1, new Puzzle(1, p1));
		elemek.put(2, new Puzzle(2, p2));
		elemek.put(3, new Puzzle(3, p3));
		elemek.put(4, new Puzzle(4, p4));
		elemek.put(5, new Puzzle(5, p5));
		elemek.put(6, new Puzzle(6, p6));
		elemek.put(7, new Puzzle(7, p7));
		elemek.put(8, new Puzzle(8, p8));
		elemek.put(9, new Puzzle(9, p9));
		elemek.put(10, new Puzzle(10, p10));
		elemek.put(11, new Puzzle(11, p11));
		elemek.put(12, new Puzzle(12, p12));
		elemek.put(13, new Puzzle(13, p13));
		elemek.put(14, new Puzzle(14, p14));
		elemek.put(15, new Puzzle(15, p15));
		elemek.put(16, new Puzzle(16, p16));

		sortingElements();

		table = new Table(DIM);
		table.setRelations(elemek);
		table.initial(0, 0, new HashSet(cornersElems));
		
		//setFixElem(1, 1, elemek.get(6));
		//setFixElem(2, 1, elemek.get(8));

		long startTime = System.currentTimeMillis();

		startSolve();

		long elapsedTime = System.currentTimeMillis() - startTime;

		System.out.println();
		System.out.println("corner elems nr: " + cornersElems.size());
		System.out.println("edge elems nr:   " + edgesElems.size());
		System.out.println("inner elems nr:  " + innerElems.size());
		calcTime(elapsedTime);
	}
}
