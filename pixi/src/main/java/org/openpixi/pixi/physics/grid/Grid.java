package org.openpixi.pixi.physics.grid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.openpixi.pixi.parallel.cellaccess.CellAction;
import org.openpixi.pixi.parallel.cellaccess.CellIterator;
import org.openpixi.pixi.physics.Settings;
import org.openpixi.pixi.physics.fields.FieldSolver;

public class Grid {

	/*
	 * TODO remove the accessors for individual cell fields and call directly the accessors on the cell
	 * TODO extract field solver to simulation
	 *	  - makes grid simpler
	 *	  - makes all the important initialization to happen at one place (in simulation)
	 */
	/**
<<<<<<< HEAD
	 * The purpose of the extra cells is twofold.
	 * 1) They represent the boundaries around the simulation area cells.
	 *	The boundaries assure that we always have a cell to interpolate to.
	 *	For example, the hardwall particle boundaries allow the particle to be outside
	 *	of the simulation area.
	 *	That means that the particle can be in a cell [numCellsX, numCellsY].
	 *	If the particle is in this cell, it will be interpolated
	 *	to the four surrounding cells from [numCellsX, numCellsY] to [numCellsX+1, numCellsY+1].
	 *	Hence, the two extra cells after the grid's end.
	 *	However, at the beginning we only need one extra cell as the particle in cell
	 *	[-1,-1] interpolates to cells from [-1,-1] to [0,0].
	 *
	 * 2) In the field solver we usually have to use left, right, top and bottom neighboring cell
	 *	to update the value of the current cell.
	 *	Without the extra cells we would have to treat each side separately in order not to get
	 *	IndexOutOfBounds exception.
	 *	With the extra cells we can comfortably iterate through the entire grid in a uniform way
	 *	using the 0 values of extra cells when calculating the fields at the sides.
=======
	 * The purpose of the extra cells is twofold. 1) They represent the
	 * boundaries around the simulation area cells. The boundaries assure that
	 * we always have a cell to interpolate to. For example, the hardwall
	 * particle boundaries allow the particle to be outside of the simulation
	 * area. That means that the particle can be in a cell [numCellsX,
	 * numCellsY]. If the particle is in this cell, it will be interpolated to
	 * the four surrounding cells from [numCellsX, numCellsY] to [numCellsX+1,
	 * numCellsY+1]. Hence, the two extra cells after the grid's end. However,
	 * at the beginning we only need one extra cell as the particle in cell
	 * [-1,-1] interpolates to cells from [-1,-1] to [0,0].
	 *
	 * 2) In the field solver we usually have to use left, right, top and bottom
	 * neighboring cell to update the value of the current cell. Without the
	 * extra cells we would have to treat each side separately in order not to
	 * get IndexOutOfBounds exception. With the extra cells we can comfortably
	 * iterate through the entire grid in a uniform way using the 0 values of
	 * extra cells when calculating the fields at the sides.
>>>>>>> 719458d68c1e3c26816ff3e46f7ed9116ca19b40
	 */
	public static final int INTERPOLATION_RADIUS = 1;
	public static final int HARDWALL_SAFETY_CELLS = 1;
	//Check comment above, but due to the CIC to particle interpolator
	//we actually need two cells at the beginning. But for an other reason than at the end.
	public static final int EXTRA_CELLS_BEFORE_GRID =
			INTERPOLATION_RADIUS + HARDWALL_SAFETY_CELLS - 1 + 1;
	public static final int EXTRA_CELLS_AFTER_GRID =
			INTERPOLATION_RADIUS + HARDWALL_SAFETY_CELLS;
	/**
	 * solver algorithm for the maxwell equations
	 */
	private FieldSolver fsolver;
	private GridBoundaryType boundaryType;
	private CellIterator cellIterator;
	private ResetChargeAction resetCharge = new ResetChargeAction();
	private ResetCurrentAction resetCurrent = new ResetCurrentAction();
	private StoreFieldsAction storeFields = new StoreFieldsAction();
	private Cell[][] cells;
	public int[][] parallelBoundaries;
	public int[] parallelBoundariesArray;
	private int mark = 1;
	/**
	 * number of cells in x direction
	 */
	private int numCellsX;
	/**
	 * number of cells in x direction
	 */
	private int numCellsY;
	/**
	 * width of each cell
	 */
	private double cellWidth;
	/**
	 * height of each cell
	 */
	private double cellHeight;

	public FieldSolver getFsolver() {
		return fsolver;
	}

	public void setFsolver(FieldSolver fsolver) {
		this.fsolver = fsolver;
	}

	public double getJx(int x, int y) {
		return cells[index(x)][index(y)].getJx();
	}

	public void addJx(int x, int y, double value) {
		cells[index(x)][index(y)].addJx(value);
	}

	public double getJy(int x, int y) {
		return cells[index(x)][index(y)].getJy();
	}

	public void addJy(int x, int y, double value) {
		cells[index(x)][index(y)].addJy(value);
	}

	public double getRho(int x, int y) {
		return cells[index(x)][index(y)].getRho();
	}

	public void setRho(int x, int y, double value) {
		cells[index(x)][index(y)].setRho(value);
	}

	public void addRho(int x, int y, double value) {
		cells[index(x)][index(y)].addRho(value);
	}

	public double getPhi(int x, int y) {
		return cells[index(x)][index(y)].getPhi();
	}

	public void setPhi(int x, int y, double value) {
		cells[index(x)][index(y)].setPhi(value);
	}

	public double getEx(int x, int y) {
		return cells[index(x)][index(y)].getEx();
	}

	public void setEx(int x, int y, double value) {
		cells[index(x)][index(y)].setEx(value);
	}

	public void addEx(int x, int y, double value) {
		cells[index(x)][index(y)].setEx(cells[index(x)][index(y)].getEx() + value);
	}

	public double getEy(int x, int y) {
		return cells[index(x)][index(y)].getEy();
	}

	public void setEy(int x, int y, double value) {
		cells[index(x)][index(y)].setEy(value);
	}

	public void addEy(int x, int y, double value) {
		cells[index(x)][index(y)].setEy(cells[index(x)][index(y)].getEy() + value);
	}

	public double getBz(int x, int y) {
		return cells[index(x)][index(y)].getBz();
	}

	public void setBz(int x, int y, double value) {
		cells[index(x)][index(y)].setBz(value);
	}

	public void addBz(int x, int y, double value) {
		cells[index(x)][index(y)].setBz(cells[index(x)][index(y)].getBz() + value);
	}

	public double getBzo(int x, int y) {
		return cells[index(x)][index(y)].getBzo();
	}

	public void setBzo(int x, int y, double value) {
		cells[index(x)][index(y)].setBzo(value);
	}

	public int getNumCellsX() {
		return numCellsX;
	}

	public int getNumCellsY() {
		return numCellsY;
	}

	public double getCellWidth() {
		return cellWidth;
	}

	public double getCellHeight() {
		return cellHeight;
	}

	public Cell getCell(int x, int y) {
		return cells[index(x)][index(y)];
	}

	public Cell[][] getCells() {
		return cells;
	}

	public Grid(Settings settings) {
		this.boundaryType = settings.getGridBoundary();

		set(settings.getGridCellsX(), settings.getGridCellsY(),
				settings.getSimulationWidth(), settings.getSimulationHeight());

		this.fsolver = settings.getGridSolver();
		this.fsolver.initializeIterator(settings.getCellIterator(), numCellsX, numCellsY);

		this.cellIterator = settings.getCellIterator();
		this.cellIterator.setExtraCellsMode(numCellsX, numCellsY);
	}

	/**
	 * In the distributed version we want to create the grid from cells which
	 * come from master; hence, this constructor. Creates grid from the given
	 * cells. The input cells have to contain also the boundary cells.
	 */
	public Grid(Settings settings, Cell[][] cells) {
		this.numCellsX = cells.length - EXTRA_CELLS_BEFORE_GRID - EXTRA_CELLS_AFTER_GRID;
		this.numCellsY = cells[0].length - EXTRA_CELLS_BEFORE_GRID - EXTRA_CELLS_AFTER_GRID;
		this.cellWidth = settings.getSimulationWidth() / numCellsX;
		this.cellHeight = settings.getSimulationHeight() / numCellsY;

		this.cells = cells;

		/*
		 * Grid and FieldSolver must have each its own cell iterator!
		 * They use different modes of iteration.
		 * While the grid iterates also over the extra cells the field solver does not.
		 */

		this.fsolver = settings.getGridSolver();
		fsolver.initializeIterator(settings.getCellIterator(), numCellsX, numCellsY);

		this.cellIterator = settings.getCellIterator();
		this.cellIterator.setExtraCellsMode(this.numCellsX, this.numCellsY);
	}

	/**
	 * Change the size of the field. TODO make sure the method can not be called
	 * in distributed version E.g. throw an exception if this is distributed
	 * version
	 */
	public void changeSize(int numCellsX, int numCellsY,
			double simWidth, double simHeight) {
		set(numCellsX, numCellsY, simWidth, simHeight);
		fsolver.changeSize(numCellsX, numCellsY);
		cellIterator.setExtraCellsMode(this.numCellsX, this.numCellsY);
	}

	/**
	 * This method is dangerous as it would not work in distributed version.
	 * TODO make sure the method can not be called in distributed version E.g.
	 * throw an exception if this is distributed version
	 */
	private void set(int numCellsX, int numCellsY,
			double simWidth, double simHeight) {

		this.numCellsX = numCellsX;
		this.numCellsY = numCellsY;
		this.cellWidth = simWidth / numCellsX;
		this.cellHeight = simHeight / numCellsY;

		createGridWithBoundaries();
	}

	private void createGridWithBoundaries() {
		cells = new Cell[getNumCellsXTotal()][getNumCellsYTotal()];
		parallelBoundaries = new int[getNumCellsXTotal()][getNumCellsYTotal()];
		parallelBoundariesArray = new int[getNumCellsXTotal() * getNumCellsYTotal()];
		// Create inner cells
		for (int x = 0; x < getNumCellsX(); x++) {
			for (int y = 0; y < getNumCellsY(); y++) {
				cells[index(x)][index(y)] = new Cell();

			}
		}
		for (int x = 0; x < getNumCellsXTotal(); x++) {
			for (int y = 0; y < getNumCellsYTotal(); y++) {
				parallelBoundaries[x][y] = 0;
			}
		}

		createBoundaryCells();
		createParallelBoundary();
	}

	private void createBoundaryCells() {
		// left boundary (with corner cells)
		for (int x = 0; x < EXTRA_CELLS_BEFORE_GRID; x++) {
			for (int y = 0; y < getNumCellsYTotal(); y++) {
				createBoundaryCell(x, y);
			}
		}
		// right boundary (with corner cells)
		for (int x = EXTRA_CELLS_BEFORE_GRID + numCellsX; x < getNumCellsXTotal(); x++) {
			for (int y = 0; y < getNumCellsYTotal(); y++) {
				createBoundaryCell(x, y);
			}
		}
		// top boundary (without corner cells)
		for (int x = EXTRA_CELLS_BEFORE_GRID; x < EXTRA_CELLS_BEFORE_GRID + numCellsX; x++) {
			for (int y = 0; y < EXTRA_CELLS_BEFORE_GRID; y++) {
				createBoundaryCell(x, y);
			}
		}
		// bottom boundary (without corner cells)
		for (int x = EXTRA_CELLS_BEFORE_GRID; x < EXTRA_CELLS_BEFORE_GRID + numCellsX; x++) {
			for (int y = EXTRA_CELLS_BEFORE_GRID + numCellsY; y < getNumCellsYTotal(); y++) {
				createBoundaryCell(x, y);
			}
		}
	}

	/**
	 * Based on the boundary type creates the boundary cell.
	 */
	private void createBoundaryCell(int x, int y) {
		if (boundaryType == GridBoundaryType.Hardwall) {
			cells[x][y] = new Cell();
		} else if (boundaryType == GridBoundaryType.Periodic) {
			int xmin = EXTRA_CELLS_BEFORE_GRID;
			int xmax = numCellsX + EXTRA_CELLS_BEFORE_GRID - 1;
			int ymin = EXTRA_CELLS_BEFORE_GRID;
			int ymax = numCellsY + EXTRA_CELLS_BEFORE_GRID - 1;

			int refX = x;
			int refY = y;
			if (x < xmin) {
				refX += numCellsX;
			} else if (x > xmax) {
				refX -= numCellsX;
			}
			if (y < ymin) {
				refY += numCellsY;
			} else if (y > ymax) {
				refY -= numCellsY;
			}

			cells[x][y] = cells[refX][refY];
		}
	}

	public void createParallelBoundary() {
		int mark = 1;

		/*
		 * Find the cells that share the same reference and mark them.
		 * If cells[i][j]==cells[a][b] then mark[i][j]==mark[a][b]
		 */
		for (int i = 0; i < getNumCellsXTotal(); i++) {
			for (int j = 0; j < getNumCellsYTotal(); j++) {
				mark++;
				for (int l = 0; l < getNumCellsXTotal(); l++) {
					for (int m = 0; m < getNumCellsYTotal(); m++) {
						if (cells[i][j] == cells[l][m] && parallelBoundaries[l][m] == 0) {
							parallelBoundaries[l][m] = mark;
						}
					}
				}
			}
		}

		/*
		 * Convert the matrix to an array 
		 */
		int k = 0;
		for (int i = 0; i < getNumCellsXTotal(); i++) {
			for (int j = 0; j < getNumCellsYTotal(); j++) {
				parallelBoundariesArray[k++] = parallelBoundaries[i][j];
			}
		}

		/*
		 * Create a HashMap where the key is the mark and the value is
		 * a list containing indexes of all the cells that have that mark.
		 */
		HashMap<Integer, Vector<Integer>> marks = new HashMap<Integer, Vector<Integer>>();
		Vector<Integer> indexes;
		for (int i = 0; i < getNumCellsXTotal() * getNumCellsYTotal(); i++) {
			mark = parallelBoundariesArray[i];
			if (!marks.containsKey(mark)) {
				indexes = new Vector<Integer>();
				indexes.add(i);
				marks.put(mark, indexes);
			} else {
				indexes = marks.get(mark);
				indexes.add(i);
				marks.put(mark, indexes);
			}
		}

		/*
		 * Create circular lists so a cell will point to the next cell
		 * that has the same mark
		 */
		int index1, index2;
		for (int i = 0; i < getNumCellsXTotal(); i++) {
			for (int j = 0; j < getNumCellsYTotal(); j++) {
				mark = parallelBoundaries[i][j]; //get a mark
				indexes = marks.get(mark);       //get the indexes of all the cells that share the mark
				for (int l = 0; l < indexes.size(); l++) {
					index1 = indexes.elementAt(l);
					if (l == indexes.size() - 1) { //the last cell will point to the first one
						parallelBoundariesArray[index1] = indexes.elementAt(0);
					} else {  //every other cell will point to the next cell in the list
						index2 = indexes.elementAt(l + 1);
						parallelBoundariesArray[index1] = index2;
					}
				}
			}
		}
	}

	public void updateGrid(double tstep) {
		storeFields();
		getFsolver().step(this, tstep);
	}

	public void resetCurrent() {
		cellIterator.execute(this, resetCurrent);
	}

	public void resetCharge() {
		cellIterator.execute(this, resetCharge);
	}

	public void storeFields() {
		cellIterator.execute(this, storeFields);
	}

	/**
	 * Maps the client index which can be negative to the real array index which
	 * has to be non-negative. The client index can be negative if the client is
	 * asking for a cell which is within the top or left boundary. (By client we
	 * mean any code which is using this class)
	 *
	 */
	private int index(int clientIdx) {
		return EXTRA_CELLS_BEFORE_GRID + clientIdx;
	}

	/**
	 * Includes the extra cells.
	 */
	public int getNumCellsXTotal() {
		return numCellsX + EXTRA_CELLS_BEFORE_GRID + EXTRA_CELLS_AFTER_GRID;
	}

	/**
	 * Includes the extra cells.
	 */
	public int getNumCellsYTotal() {
		return numCellsY + EXTRA_CELLS_BEFORE_GRID + EXTRA_CELLS_AFTER_GRID;
	}

	private class ResetCurrentAction implements CellAction {

		public void execute(Grid grid, int x, int y) {
			grid.getCell(x, y).resetCurrent();
		}
	}

	private class ResetChargeAction implements CellAction {

		public void execute(Grid grid, int x, int y) {
			grid.getCell(x, y).resetCharge();
		}
	}

	private class StoreFieldsAction implements CellAction {

		public void execute(Grid grid, int x, int y) {
			grid.getCell(x, y).storeFields();
		}
	}
}
